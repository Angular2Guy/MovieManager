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
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.CastDto;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
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

@Transactional
@Service
public class MovieService {
	private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);
	private final MovieRepository movieRep;
	private final CastRepository castRep;
	private final ActorRepository actorRep;
	private final GenereRepository genereRep;
	private final UserDetailsMgmtService auds;
	private final DefaultMapper mapper;
	private final MovieDbRestClient movieDbRestClient;

	public MovieService(MovieRepository movieRep, CastRepository castRep, ActorRepository actorRep,
			GenereRepository genereRep, UserDetailsMgmtService auds, DefaultMapper mapper, MovieDbRestClient movieDbRestClient) {
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

	public List<Movie> findMoviesByGenere(Long id) {
		List<Movie> result = this.movieRep.findByGenereId(id, this.auds.getCurrentUser().getId());
		return result;
	}

	public Optional<Movie> findMovieById(Long id) {
		Optional<Movie> result = this.movieRep.findById(id);
		return result;
	}

	public boolean deleteMovieById(Long id) {
		boolean result = true;
		try {
			User user = this.auds.getCurrentUser();
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

	public List<Movie> findMovie(String title) {
		List<Movie> result = this.movieRep.findByTitle(title, this.auds.getCurrentUser().getId());
		return result;
	}

	public List<Movie> findMoviesByPage(Integer page) {
		User currentUser = this.auds.getCurrentUser();
		List<Movie> result = this.movieRep.findMoviesByPage(currentUser.getId(), PageRequest.of((page - 1), 10));
		return result;
	}

	public List<MovieDto> findImportMovie(String title) {
		User user = this.auds.getCurrentUser();
		RestTemplate restTemplate = new RestTemplate();
		String queryStr = this.createQueryStr(title);
		WrapperMovieDto wrMovie = restTemplate
				.getForObject(
						"https://api.themoviedb.org/3/search/movie?api_key=" + user.getMoviedbkey()
								+ "&language=en-US&query=" + queryStr + "&page=1&include_adult=false",
						WrapperMovieDto.class);
		List<MovieDto> result = Arrays.asList(wrMovie.getResults());
		return result;
	}

	public boolean cleanup() {
		this.movieRep.findUnusedMovies().forEach(
				movie -> LOG.info(String.format("Unused Movie id: %d title: %s", movie.getId(), movie.getTitle())));
		return true;
	}

	public boolean importMovie(String title, int number) throws InterruptedException {
		User user = this.auds.getCurrentUser();
		LOG.info("Start import");
		RestTemplate restTemplate = new RestTemplate();
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
		LOG.info("Start import Movie");
		String queryStr = this.createQueryStr(title);
		WrapperMovieDto wrMovie = this.movieDbRestClient.fetchMovie(user.getMoviedbkey(), queryStr);
		Movie movieEntity = this.movieRep.findByMovieId(wrMovie.getResults()[number].getMovieId(), user.getId())
				.orElse(null);
		if (movieEntity == null) {
			LOG.info("Movie not found by id");
			List<Movie> movies = this.movieRep.findByTitleAndRelDate(wrMovie.getResults()[number].getTitle(),
					wrMovie.getResults()[number].getReleaseDate(), this.auds.getCurrentUser().getId());
			if (!movies.isEmpty()) {
				LOG.info("Movie found by Title and Reldate");
				movieEntity = movies.get(0);
				movieEntity.setMovieId(wrMovie.getResults()[number].getId());
			} else {
				LOG.info("creating new Movie");
				movieEntity = this.mapper.convert(wrMovie.getResults()[number]);
				for (int genId : wrMovie.getResults()[number].getGeneres()) {
					Optional<Genere> myResult = generes.stream().filter(
							myGenere -> Long.valueOf(Integer.valueOf(genId).longValue()).equals(myGenere.getGenereId()))
							.findFirst();
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
		WrapperCastDto wrCast = this.movieDbRestClient.fetchCast(user.getMoviedbkey(), wrMovie.getResults()[number].getId());
		if (movieEntity.getCast().isEmpty()) {
			for (CastDto c : wrCast.getCast()) {
				LOG.info("Creating new cast for movie");
				Cast castEntity = this.mapper.convert(c);
				movieEntity.getCast().add(castEntity);
				castEntity.setMovie(movieEntity);
				ActorDto actor = this.movieDbRestClient.fetchActor(user.getMoviedbkey(), c.getId());				
				Optional<Actor> actorOpt = this.actorRep.findByActorId(actor.getActorId(), user.getId());
				Actor actorEntity = actorOpt.isPresent() ? actorOpt.get() : this.mapper.convert(actor);
				castEntity = this.castRep.save(castEntity);
				if (actorOpt.isEmpty()) {
					actorEntity = this.actorRep.save(actorEntity);
					actorEntity.getUsers().add(user);
				}
				actorEntity.getCasts().add(castEntity);
				castEntity.setActor(actorEntity);
				Thread.sleep(300);
			}
		} else {
			for (CastDto c : wrCast.getCast()) {
				LOG.info("update cast for movie");
				ActorDto actor = this.movieDbRestClient.fetchActor(user.getMoviedbkey(), c.getId());				
				Optional<Actor> actorOpt = this.actorRep.findByActorId(actor.getActorId(), user.getId());
				Actor actorEntity = actorOpt.get();
				if (!actorEntity.getUsers().contains(user)) {
					actorEntity.getUsers().add(user);
				}
				Thread.sleep(300);
			}
		}
		return true;
	}

	private String createQueryStr(String str) {
		return str.replace(" ", "%20");
	}
}
