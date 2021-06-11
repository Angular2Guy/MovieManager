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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import ch.xxx.moviemanager.domain.model.Actor;
import ch.xxx.moviemanager.domain.model.ActorRepository;
import ch.xxx.moviemanager.domain.model.Cast;
import ch.xxx.moviemanager.domain.model.CastRepository;
import ch.xxx.moviemanager.domain.model.Genere;
import ch.xxx.moviemanager.domain.model.GenereRepository;
import ch.xxx.moviemanager.domain.model.Movie;
import ch.xxx.moviemanager.domain.model.MovieRepository;
import ch.xxx.moviemanager.domain.model.User;
import ch.xxx.moviemanager.domain.model.UserRepository;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.model.ActorDto;
import ch.xxx.moviemanager.usecase.model.CastDto;
import ch.xxx.moviemanager.usecase.model.GenereDto;
import ch.xxx.moviemanager.usecase.model.MovieDto;
import ch.xxx.moviemanager.usecase.model.UserDto;
import ch.xxx.moviemanager.usecase.model.WrapperCastDto;
import ch.xxx.moviemanager.usecase.model.WrapperGenereDto;
import ch.xxx.moviemanager.usecase.model.WrapperMovieDto;

@Transactional
@Service
public class MovieService {
	private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);
	private final MovieRepository movieRep;
	private final CastRepository castRep;
	private final ActorRepository actorRep;
	private final GenereRepository genereRep;
	private final UserRepository userRep;
	private final AppUserDetailsService auds;
	private final DefaultMapper mapper;

	public MovieService(MovieRepository movieRep, CastRepository castRep, ActorRepository actorRep,
			GenereRepository genereRep, UserRepository userRep, AppUserDetailsService auds,
			DefaultMapper mapper) {
		this.auds = auds;
		this.actorRep = actorRep;
		this.castRep = castRep;
		this.genereRep = genereRep;
		this.movieRep = movieRep;
		this.userRep = userRep;
		this.mapper = mapper;
	}

	public List<GenereDto> findAllGeneres() {
		List<GenereDto> result = this.genereRep.findAll().stream().map(gen -> this.mapper.convert(gen))
				.collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMoviesByGenere(Long id) {
		List<MovieDto> result = this.movieRep.findByGenereId(id, this.auds.getCurrentUser().getId()).stream()
				.map(m -> this.mapper.convert(m)).collect(Collectors.toList());
		return result;
	}

	public Optional<MovieDto> findMovieById(Long id) {
		Optional<MovieDto> res = Optional.empty();
		Optional<Movie> result = this.movieRep.findById(id);
		if (result.isPresent()) {
			MovieDto movieDto = this.mapper.convert(result.get());
			res = Optional.of(movieDto);
		}
		return res;
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

	public List<MovieDto> findMovie(String title) {
		List<MovieDto> result = this.movieRep.findByTitle(title, this.auds.getCurrentUser().getId()).stream()
				.map(m -> this.mapper.convert(m)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMoviesByPage(Integer page) {
		User currentUser = this.auds.getCurrentUser();
		List<MovieDto> result = this.movieRep.findMoviesByPage(currentUser.getId(), PageRequest.of((page - 1), 10))
				.stream().map(m -> this.mapper.convert(m)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findImportMovie(String title) {
		User user = this.userRep
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
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
		
		return true;
	}
	
	public boolean importMovie(String title, int number) throws InterruptedException {
		User user = this.auds.getCurrentUser();
		LOG.info("Start import");
		RestTemplate restTemplate = new RestTemplate();
		LOG.info("Start import generes");
		WrapperGenereDto result = restTemplate.getForObject(
				"https://api.themoviedb.org/3/genre/movie/list?api_key=" + user.getMoviedbkey() + "&language=en-US",
				WrapperGenereDto.class);
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
		WrapperMovieDto wrMovie = restTemplate
				.getForObject(
						"https://api.themoviedb.org/3/search/movie?api_key=" + user.getMoviedbkey()
								+ "&language=en-US&query=" + queryStr + "&page=1&include_adult=false",
						WrapperMovieDto.class);		
		Movie movieEntity = this.movieRep
				.findByMovieId(wrMovie.getResults()[number].getMovieId(), user.getId()).orElse(null);
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
		WrapperCastDto wrCast = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"
				+ wrMovie.getResults()[number].getId() + "/credits?api_key=" + user.getMoviedbkey(),
				WrapperCastDto.class);
		if (movieEntity.getCast().isEmpty()) {
			for (CastDto c : wrCast.getCast()) {
				LOG.info("Creating new cast for movie");
				Cast castEntity = this.mapper.convert(c);
				movieEntity.getCast().add(castEntity);
				castEntity.setMovie(movieEntity);
				ActorDto actor = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + c.getId()
						+ "?api_key=" + user.getMoviedbkey() + "&language=en-US", ActorDto.class);
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
				ActorDto actor = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + c.getId()
						+ "?api_key=" + user.getMoviedbkey() + "&language=en-US", ActorDto.class);
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
