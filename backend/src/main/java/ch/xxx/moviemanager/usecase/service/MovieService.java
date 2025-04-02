/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.usecase.service;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;
import com.google.crypto.tink.daead.DeterministicAeadConfig;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.common.CommonUtils;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.CastDto;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.domain.model.dto.MovieFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.CastRepository;
import ch.xxx.moviemanager.domain.model.entity.Genere;
import ch.xxx.moviemanager.domain.model.entity.GenereRepository;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import jakarta.annotation.PostConstruct;

@Transactional
@Service
public class MovieService {
	private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);
	private final MovieRepository movieRep;
	private final CastRepository castRep;
	private final ActorRepository actorRep;
	private final GenereRepository genereRep;
	private final UserDetailService userDetailService;
	private final DefaultMapper mapper;
	private final MovieDbRestClient movieDbRestClient;
	@Value("${tink.json.key}")
	private String tinkJsonKey;
	private DeterministicAead daead;

	public MovieService(MovieRepository movieRep, CastRepository castRep, ActorRepository actorRep,
			GenereRepository genereRep, UserDetailService userDetailService, DefaultMapper mapper,
			MovieDbRestClient movieDbRestClient) {
		this.userDetailService = userDetailService;
		this.actorRep = actorRep;
		this.castRep = castRep;
		this.genereRep = genereRep;
		this.movieRep = movieRep;
		this.mapper = mapper;
		this.movieDbRestClient = movieDbRestClient;
	}

	@PostConstruct
	public void init() throws GeneralSecurityException {
		DeterministicAeadConfig.register();
		KeysetHandle handle = TinkJsonProtoKeysetFormat.parseKeyset(this.tinkJsonKey, InsecureSecretKeyAccess.get());
		this.daead = handle.getPrimitive(DeterministicAead.class);
	}

	public List<Genere> findAllGeneres() {
		List<Genere> result = this.genereRep.findAll();
		return result;
	}

	public List<Movie> findMoviesByGenereId(Long id, String bearerStr) {
		List<Movie> result = this.movieRep.findByGenereId(id, this.userDetailService.getCurrentUser(bearerStr).getId());
		return result;
	}

	public Optional<Movie> findMovieById(Long id, String bearerStr) {
		final var user = this.userDetailService.getCurrentUser(bearerStr);
		Optional<Movie> result = this.movieRep.findById(id)
				.filter(myMovie -> myMovie.getUsers().stream().anyMatch(myUser -> myUser.getId().equals(user.getId())));
		return result;
	}

	public boolean deleteMovieById(Long id, String bearerStr) {
		boolean result = true;
		try {
			final User user = this.userDetailService.getCurrentUser(bearerStr);
			Optional<Movie> movieOpt = this.movieRep.findById(id);
			if (movieOpt.stream().map(myMovie -> myMovie.getUsers()).anyMatch(myUsers -> myUsers.contains(user))) {
				Movie movie = movieOpt.get();
				movie.getUsers().remove(user);
				if (movie.getUsers().isEmpty()) {
					for (Cast c : movie.getCast()) {
						c.getActor().getCasts().remove(c);
						if (c.getActor().getCasts().isEmpty()) {
							this.actorRep.deleteById(c.getActor().getId());
						}
					}
					this.movieRep.deleteById(id);
				}
			}
		} catch (RuntimeException re) {
			LOG.error("Delete movie failed.", re);
			result = false;
		}
		return result;
	}

	public List<Movie> findMovie(String title, String bearerStr) {
		PageRequest pageRequest = PageRequest.of(0, 15, Sort.by("title").ascending());
		List<Movie> result = this.movieRep.findByTitle(title, this.userDetailService.getCurrentUser(bearerStr).getId(),
				pageRequest);
		return result;
	}

	public List<Movie> findMoviesByPage(Integer page, String bearerStr) {
		User currentUser = this.userDetailService.getCurrentUser(bearerStr);
		List<Movie> result = this.movieRep.findMoviesByPage(currentUser.getId(), PageRequest.of((page - 1), 10));
		result = result.stream().flatMap(movie -> Stream.of(this.movieRep.findByIdWithCollections(movie.getId())))
				.flatMap(Optional::stream).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findImportMovie(String title, String bearerStr) throws GeneralSecurityException {
		User user = this.userDetailService.getCurrentUser(bearerStr);
		String queryStr = this.createQueryStr(title);
		WrapperMovieDto wrMovie = this.movieDbRestClient
				.fetchImportMovie(this.decrypt(user.getMoviedbkey(), user.getUuid()), queryStr);
		List<MovieDto> result = Arrays.asList(wrMovie.getResults());
		return result;
	}

	public boolean cleanup() {
		var unusedMovies = this.movieRep.findUnusedMovies().stream().peek(
				movie -> LOG.info(String.format("Unused Movie id: %d title: %s", movie.getId(), movie.getTitle()))).collect(Collectors.toList());
		unusedMovies.forEach(movie -> {
			this.movieRep.deleteById(movie.getId());
			movie.getCast().forEach(cast -> {
				cast.getActor().getCasts().remove(cast);
				cast.setActor(null);
				cast.setMovie(null);
			});			
			movie.getCast().clear();
		});
		return true;
	}

	private String decrypt(String cipherText, String uuid) throws GeneralSecurityException {
		String result = new String(daead.decryptDeterministically(Base64.getDecoder().decode(cipherText),
				uuid.getBytes(Charset.defaultCharset())));
		return result;
	}

	public boolean importMovie(int movieDbId, String bearerStr) throws InterruptedException, GeneralSecurityException {
		User user = this.userDetailService.getCurrentUser(bearerStr);
		LOG.info("Start import");
		LOG.info("Start import generes");
		WrapperGenereDto result = this.movieDbRestClient
				.fetchAllGeneres(this.decrypt(user.getMoviedbkey(), user.getUuid()));
		List<Genere> generes = new ArrayList<>(this.genereRep.findAll());
		generes.addAll(this.findMissingGeneres(result, generes));

		LOG.info("Start import Movie with Id: {movieDbId}", movieDbId);
		MovieDto movieDto = this.movieDbRestClient.fetchMovie(this.decrypt(user.getMoviedbkey(), user.getUuid()),
				movieDbId);
		Optional<Movie> movieOpt = this.movieRep.findByMovieId(movieDto.getMovieId(), user.getId());
		Movie movieEntity = movieOpt.orElseGet(() -> {
			LOG.info("Movie not found by id");
			Movie result1 = this.movieRep
					.findByTitleAndRelDate(movieDto.getTitle(), movieDto.getReleaseDate(),
							this.userDetailService.getCurrentUser(bearerStr).getId())
					.stream().findFirst().orElseGet(() -> this.createNewMovie(generes, movieDto));
			result1.setMovieId(movieDto.getId());
			return result1;
		});

		if (!movieEntity.getUsers().contains(user)) {
			LOG.info("adding user to movie");
			movieEntity.getUsers().add(user);
		}
		WrapperCastDto wrCast = this.movieDbRestClient.fetchCast(this.decrypt(user.getMoviedbkey(), user.getUuid()),
				movieDto.getId());
		if (movieEntity.getCast().isEmpty()) {
			for (CastDto cDto : wrCast.getCast()) {
				LOG.info("Creating new cast for movie");				
				if (checkNullOrBlank(cDto.getCharacter())  || 
						checkNullOrBlank(cDto.getName())) {
					continue;
				}
				Cast castEntity = this.mapper.convert(cDto);
				movieEntity.getCast().add(castEntity);
				castEntity.setMovie(movieEntity);
				ActorDto actor = this.movieDbRestClient.fetchActor(this.decrypt(user.getMoviedbkey(), user.getUuid()),
						cDto.getId(), 300L);
				Actor actorEntity = this.actorRep.findByActorId(actor.getId(), user.getId())
						.orElse(this.mapper.convert(actor));
				castEntity = this.castRep.save(castEntity);
				actorEntity = this.actorRep.save(actorEntity);
				if (!actorEntity.getUsers().contains(user)) {
					actorEntity.getUsers().add(user);
				}
				actorEntity.getCasts().add(castEntity);
				castEntity.setActor(actorEntity);
			}
		} else {
			for (CastDto cDto : wrCast.getCast()) {
				LOG.info("update cast for movie");
				ActorDto actor = this.movieDbRestClient.fetchActor(this.decrypt(user.getMoviedbkey(), user.getUuid()),
						cDto.getId(), 300L);
				Actor actorEntity = this.actorRep.findByActorId(actor.getId(), user.getId())
						.orElse(this.mapper.convert(actor));
				actorEntity = this.actorRep.save(actorEntity);
				if (!actorEntity.getUsers().contains(user)) {
					actorEntity.getUsers().add(user);
				}
			}
		}
		LOG.info("Finished import");
		return true;
	}

	private boolean checkNullOrBlank(String value) {
		return Optional.ofNullable(value).stream().filter(Predicate.not(String::isBlank)).findFirst().isEmpty();
	}

	private Movie createNewMovie(List<Genere> generes, MovieDto movieDto) {
		LOG.info("creating new Movie");
		final var result2 = this.mapper.convert(movieDto);
		movieDto.getGenres().stream().map(myGenere -> myGenere.getId()).forEach(genId -> {
			generes.stream().filter(myGenere -> genId.equals(myGenere.getGenereId())).findFirst()
					.ifPresent(myResult -> {
						result2.getGeneres().add(myResult);
						myResult.getMovies().add(result2);
					});
		});
		return this.movieRep.save(result2);
	}

	private List<Genere> findMissingGeneres(WrapperGenereDto result, List<Genere> generes) {
		return Arrays.asList(result.getGenres()).stream()
				.filter(gDto -> generes.stream()
						.filter(myGenere -> Optional.ofNullable(myGenere.getGenereId()).stream()
								.anyMatch(myGenereId -> myGenereId.equals(gDto.getId())))
						.findFirst().isEmpty())
				.flatMap(myDto -> Stream.of(genereRep.save(this.mapper.convert(myDto)))).toList();
	}

	private String createQueryStr(String str) {
		return str.replace(" ", "%20");
	}

	public List<Movie> findMoviesByFilterCriteria(String bearerStr, MovieFilterCriteriaDto filterCriteriaDto) {
		List<Movie> jpaMovies = this.movieRep.findByFilterCriteria(filterCriteriaDto,
				this.userDetailService.getCurrentUser(bearerStr).getId());
		List<Movie> ftMovies = this.findMoviesBySearchTerm(bearerStr, filterCriteriaDto.getSearchTermDto());
		List<Movie> results = jpaMovies;
		if (!ftMovies.isEmpty()) {
			Collection<Long> dublicates = CommonUtils
					.findDublicates(Stream.of(jpaMovies, ftMovies).flatMap(List::stream).toList());
			results = Stream.of(jpaMovies, ftMovies).flatMap(List::stream)
					.filter(myMovie -> CommonUtils.filterForDublicates(myMovie, dublicates)).toList();
			// remove dublicates
			results = results.isEmpty() ? ftMovies : List.copyOf(CommonUtils.filterDublicates(results));
		}
		return results.subList(0, results.size() > 50 ? 50 : results.size());
	}

	public List<Movie> findMoviesBySearchTerm(String bearerStr, SearchTermDto searchTermDto) {
		List<Movie> filteredMovies = List.of();
		if (Optional.ofNullable(searchTermDto.getSearchPhraseDto().getPhrase()).stream().anyMatch(
				myPhrase -> Optional.ofNullable(myPhrase).stream().anyMatch(phrase -> phrase.trim().length() > 2))
				|| !Arrays.asList(searchTermDto.getSearchStringDtos()).isEmpty()) {
			List<Movie> movies = Arrays.asList(searchTermDto.getSearchStringDtos()).isEmpty()
					? this.movieRep.findMoviesByPhrase(searchTermDto.getSearchPhraseDto())
					: this.movieRep.findMoviesBySearchStrings(Arrays.asList(searchTermDto.getSearchStringDtos()));
			filteredMovies = movies.stream()
					.filter(myMovie -> myMovie.getUsers().stream().anyMatch(
							myUser -> myUser.getId().equals(this.userDetailService.getCurrentUser(bearerStr).getId())))
					.toList();
		}
		return filteredMovies;
	}
}
