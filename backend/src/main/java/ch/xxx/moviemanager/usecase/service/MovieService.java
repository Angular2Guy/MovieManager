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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.common.CommonUtils;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.CastDto;
import ch.xxx.moviemanager.domain.model.dto.MovieFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.CastRepository;
import ch.xxx.moviemanager.domain.model.entity.EntityBase;
import ch.xxx.moviemanager.domain.model.entity.Genere;
import ch.xxx.moviemanager.domain.model.entity.GenereRepository;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;

@Transactional
@Service
public class MovieService {
	private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);
	private final MovieRepository movieRep;
	private final CastRepository castRep;
	private final ActorRepository actorRep;
	private final GenereRepository genereRep;
	private final UserDetailMgmtService auds;
	private final DefaultMapper mapper;
	private final MovieDbRestClient movieDbRestClient;

	public MovieService(MovieRepository movieRep, CastRepository castRep, ActorRepository actorRep,
			GenereRepository genereRep, UserDetailMgmtService auds, DefaultMapper mapper,
			MovieDbRestClient movieDbRestClient) {
		this.auds = auds;
		this.actorRep = actorRep;
		this.castRep = castRep;
		this.genereRep = genereRep;
		this.movieRep = movieRep;
		this.mapper = mapper;
		this.movieDbRestClient = movieDbRestClient;
	}

	public List<Genere> findAllGeneres() {
		List<Genere> result = this.genereRep.findAll();
		return result;
	}

	public List<Movie> findMoviesByGenere(Long id, String bearerStr) {
		List<Movie> result = this.movieRep.findByGenereId(id, this.auds.getCurrentUser(bearerStr).getId());
		return result;
	}

	public Optional<Movie> findMovieById(Long id) {
		Optional<Movie> result = this.movieRep.findById(id);
		return result;
	}

	public boolean deleteMovieById(Long id, String bearerStr) {
		boolean result = true;
		try {
			User user = this.auds.getCurrentUser(bearerStr);
			Optional<Movie> movieOpt = this.movieRep.findById(id);
			if (movieOpt.isPresent() && movieOpt.get().getUsers().contains(user)) {
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
			result = false;
		}
		return result;
	}

	public List<Movie> findMovie(String title, String bearerStr) {
		PageRequest pageRequest = PageRequest.of(0, 15, Sort.by("title").ascending());
		List<Movie> result = this.movieRep.findByTitle(title, this.auds.getCurrentUser(bearerStr).getId(), pageRequest);
		return result;
	}

	public List<Movie> findMoviesByPage(Integer page, String bearerStr) {
		User currentUser = this.auds.getCurrentUser(bearerStr);
		List<Movie> result = this.movieRep.findMoviesByPage(currentUser.getId(), PageRequest.of((page - 1), 10));
		result = result.stream().flatMap(movie -> Stream.of(this.movieRep.findByIdWithCollections(movie.getId())))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findImportMovie(String title, String bearerStr) {
		User user = this.auds.getCurrentUser(bearerStr);
		String queryStr = this.createQueryStr(title);
		WrapperMovieDto wrMovie = this.movieDbRestClient.fetchImportMovie(user.getMoviedbkey(), queryStr);
		List<MovieDto> result = Arrays.asList(wrMovie.getResults());
		return result;
	}

	public boolean cleanup() {
		this.movieRep.findUnusedMovies().forEach(
				movie -> LOG.info(String.format("Unused Movie id: %d title: %s", movie.getId(), movie.getTitle())));
		return true;
	}

	public boolean importMovie(int movieDbId, String bearerStr) throws InterruptedException {
		User user = this.auds.getCurrentUser(bearerStr);
		LOG.info("Start import");
		LOG.info("Start import generes");
		WrapperGenereDto result = this.movieDbRestClient.fetchAllGeneres(user.getMoviedbkey());
		List<Genere> generes = new ArrayList<>(this.genereRep.findAll());
		for (GenereDto g : result.getGenres()) {
			Genere genereEntity = generes.stream()
					.filter(myGenere -> myGenere.getGenereId() != null && myGenere.getGenereId().equals(g.getId()))
					.findFirst().orElse(this.mapper.convert(g));
			if (genereEntity.getId() == null) {
				genereEntity = genereRep.save(genereEntity);
				generes.add(genereEntity);

			}
		}
		LOG.info("Start import Movie with Id: {movieDbId}", movieDbId);
		MovieDto movieDto = this.movieDbRestClient.fetchMovie(user.getMoviedbkey(), movieDbId);
		Movie movieEntity = this.movieRep.findByMovieId(movieDto.getMovieId(), user.getId()).orElse(null);
		if (movieEntity == null) {
			LOG.info("Movie not found by id");
			List<Movie> movies = this.movieRep.findByTitleAndRelDate(movieDto.getTitle(), movieDto.getReleaseDate(),
					this.auds.getCurrentUser(bearerStr).getId());
			if (!movies.isEmpty()) {
				LOG.info("Movie found by Title and Reldate");
				movieEntity = movies.get(0);
				movieEntity.setMovieId(movieDto.getId());
			} else {
				LOG.info("creating new Movie");
				movieEntity = this.mapper.convert(movieDto);
				movieEntity.setMovieId(movieDto.getId());
				for (Long genId : movieDto.getGenres().stream().map(myGenere -> myGenere.getId()).toList()) {
					Optional<Genere> myResult = generes.stream()
							.filter(myGenere -> genId.equals(myGenere.getGenereId())).findFirst();
					if (myResult.isPresent()) {
						movieEntity.getGeneres().add(myResult.get());
						myResult.get().getMovies().add(movieEntity);
					}
				}
				movieEntity = this.movieRep.save(movieEntity);
			}
		}
		if (!movieEntity.getUsers().contains(user)) {
			LOG.info("adding user to movie");
			movieEntity.getUsers().add(user);
		}
		WrapperCastDto wrCast = this.movieDbRestClient.fetchCast(user.getMoviedbkey(), movieDto.getId());
		if (movieEntity.getCast().isEmpty()) {
			for (CastDto c : wrCast.getCast()) {
				LOG.info("Creating new cast for movie");
				Cast castEntity = this.mapper.convert(c);
				movieEntity.getCast().add(castEntity);
				castEntity.setMovie(movieEntity);
				ActorDto actor = this.movieDbRestClient.fetchActor(user.getMoviedbkey(), c.getId(), 300L);
				Optional<Actor> actorOpt = this.actorRep.findByActorId(actor.getActorId(), user.getId());
				Actor actorEntity = actorOpt.isPresent() ? actorOpt.get() : this.mapper.convert(actor);
				castEntity = this.castRep.save(castEntity);
				if (actorOpt.isEmpty()) {
					actorEntity = this.actorRep.save(actorEntity);
					actorEntity.getUsers().add(user);
				}
				actorEntity.getCasts().add(castEntity);
				castEntity.setActor(actorEntity);
			}
		} else {
			for (CastDto c : wrCast.getCast()) {
				LOG.info("update cast for movie");
				ActorDto actor = this.movieDbRestClient.fetchActor(user.getMoviedbkey(), c.getId(), 300L);
				Optional<Actor> actorOpt = this.actorRep.findByActorId(actor.getActorId(), user.getId());
				Actor actorEntity = actorOpt.get();
				if (!actorEntity.getUsers().contains(user)) {
					actorEntity.getUsers().add(user);
				}
			}
		}
		return true;
	}

	private String createQueryStr(String str) {
		return str.replace(" ", "%20");
	}

	public List<Movie> findMoviesByFilterCriteria(String bearerStr, MovieFilterCriteriaDto filterCriteriaDto) {
		List<Movie> jpaMovies = this.movieRep.findByFilterCriteria(filterCriteriaDto,
				this.auds.getCurrentUser(bearerStr).getId());
		SearchTermDto searchTermDto = new SearchTermDto();
		searchTermDto.setSearchPhraseDto(filterCriteriaDto.getSearchPhraseDto());
		List<Movie> ftMovies = this.findMoviesBySearchTerm(bearerStr, searchTermDto);
		List<Movie> results = jpaMovies;
		if (filterCriteriaDto.getSearchPhraseDto() != null
				&& !Objects.isNull(filterCriteriaDto.getSearchPhraseDto().getPhrase())
				&& filterCriteriaDto.getSearchPhraseDto().getPhrase().length() > 2) {
			Collection<Long> dublicates = CommonUtils
					.findDublicates(Stream.of(jpaMovies, ftMovies).flatMap(List::stream).toList());
			results = Stream.of(jpaMovies, ftMovies).flatMap(List::stream)
					.filter(myMovie -> CommonUtils.filterDublicates(myMovie, dublicates)).toList();
			// remove dublicates
			results = List.copyOf(results.stream()
					.collect(Collectors.toMap(Movie::getId, d -> d, (Movie x, Movie y) -> x == null ? y : x)).values());
		}
		return List.copyOf(results);
	}

	public List<Movie> findMoviesBySearchTerm(String bearerStr, SearchTermDto searchTermDto) {
		List<Movie> movies = searchTermDto.getSearchPhraseDto() != null
				? this.movieRep.findMoviesByPhrase(searchTermDto.getSearchPhraseDto())
				: this.movieRep.findMoviesBySearchStrings(searchTermDto.getSearchStringDtos());
		List<Movie> filteredMovies = movies.stream()
				.filter(myMovie -> myMovie.getUsers().stream()
						.anyMatch(myUser -> myUser.getId().equals(this.auds.getCurrentUser(bearerStr).getId())))
				.toList();
		return filteredMovies;
	}
}
