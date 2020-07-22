package ch.xxx.moviemanager.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import ch.xxx.moviemanager.dto.ActorDto;
import ch.xxx.moviemanager.dto.CastDto;
import ch.xxx.moviemanager.dto.GenereDto;
import ch.xxx.moviemanager.dto.MovieDto;
import ch.xxx.moviemanager.dto.UserDto;
import ch.xxx.moviemanager.dto.WrapperCastDto;
import ch.xxx.moviemanager.dto.WrapperGenereDto;
import ch.xxx.moviemanager.dto.WrapperMovieDto;
import ch.xxx.moviemanager.model.Actor;
import ch.xxx.moviemanager.model.Cast;
import ch.xxx.moviemanager.model.Genere;
import ch.xxx.moviemanager.model.Movie;
import ch.xxx.moviemanager.model.User;
import ch.xxx.moviemanager.repository.CrudActorRepository;
import ch.xxx.moviemanager.repository.CrudCastRepository;
import ch.xxx.moviemanager.repository.CrudGenereRepository;
import ch.xxx.moviemanager.repository.CrudMovieRepository;
import ch.xxx.moviemanager.repository.CrudUserRepository;
import ch.xxx.moviemanager.repository.CustomRepository;

@Transactional
@Service
public class MovieManagerService {
	private static final Logger LOG = LoggerFactory.getLogger(MovieManagerService.class);
	@Autowired
	private CrudMovieRepository crudMovieRep;
	@Autowired
	private CrudCastRepository crudCastRep;
	@Autowired
	private CrudActorRepository crudActorRep;
	@Autowired
	private CrudGenereRepository crudGenereRep;
	@Autowired
	private CrudUserRepository crudUserRep;
	@Autowired
	private CustomRepository customRep;
	@Autowired
	private AppUserDetailsService auds;

	public boolean saveUser(UserDto userDto) {
		try {
			this.auds.loadUserByUsername(userDto.getUsername());
		} catch (UsernameNotFoundException e) {
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			User user = new User();
			user.setMoviedbkey(userDto.getMoviedbkey());
			user.setPassword(encoder.encode(userDto.getPassword()));
			user.setRoles("ROLE_USER");
			user.setUsername(userDto.getUsername());
			this.crudUserRep.save(user);
			return true;
		}
		return false;
	}

	public List<GenereDto> allGeneres() {
		List<GenereDto> result = this.crudGenereRep.findAll().stream()
				.map(gen -> Converter.convert(gen)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMoviesByGenere(Long id) {
		List<MovieDto> result = this.crudMovieRep.findByGenereId(id, this.getCurrentUser().getId()).stream()
				.map(m -> Converter.convert(m)).collect(Collectors.toList());
		return result;
	}

	public Optional<MovieDto> findMovieById(Long id) {
		Optional<MovieDto> res = Optional.empty();
		Optional<Movie> result = this.crudMovieRep.findById(id);
		if (result.isPresent()) {
			MovieDto movieDto = Converter.convert(result.get());
			res = Optional.of(movieDto);
		}
		return res;
	}

	public boolean deleteMovieById(Long id) {
		boolean result = true;
		try {
			User user = getCurrentUser();
			Movie movie = this.crudMovieRep.getOne(id);
			movie.getUsers().remove(user);
			if (movie.getUsers().isEmpty()) {
				for (Cast c : movie.getCast()) {
					c.getActor().getCasts().remove(c);
					if (c.getActor().getCasts().isEmpty()) {
						this.crudActorRep.deleteById(c.getActor().getId());
					}
				}
				this.crudMovieRep.deleteById(id);
			}
		} catch (RuntimeException re) {
			result = false;
		}
		return result;
	}

	public Optional<ActorDto> findActorById(Long id) {
		Optional<Actor> result = this.crudActorRep.findById(id);
		Optional<ActorDto> res = Optional.empty();
		if (result.isPresent()) {
			User user = getCurrentUser();			
			List<Cast> casts = result.get().getCasts();
			List<Cast> myCasts = result.get().getCasts().stream().filter(c -> c.getMovie().getUsers().contains(user))
					.collect(Collectors.toList());
			result.get().setCasts(myCasts);
			ActorDto actorDto = Converter.convert(result.get());
			result.get().setCasts(casts);
			res = Optional.of(actorDto);
		}
		return res;
	}

	public List<ActorDto> findActor(String name) {			
		List<ActorDto> result = this.crudActorRep.findByActorName(name, getCurrentUser().getId()).stream()
			.map(a -> Converter.convert(a)).collect(Collectors.toList());
//		List<ActorDto> result = this.customRep.findByActorName(name).stream()
//				.map(a -> Converter.convert(a)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMovie(String title) {		
		List<MovieDto> result = this.crudMovieRep.findByTitle(title, this.getCurrentUser().getId()).stream()
				.map(m -> Converter.convert(m)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMoviesByPage(Integer page) {
		List<MovieDto> result = this.customRep.findMoviesByPage(page).stream()
				.map(m -> Converter.convert(m)).collect(Collectors.toList());
		return result;
	}
	
	public List<ActorDto> findActorsByPage(Integer page) {
		List<ActorDto> result = this.customRep.findActorsByPage(page).stream()
				.map(a -> Converter.convert(a)).collect(Collectors.toList());
		return result;
	}
	
	public List<MovieDto> findImportMovie(String title) {
		User user = this.crudUserRep
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

	public boolean importMovie(String title, int number) throws InterruptedException {
		User user = this.crudUserRep
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
		LOG.info("Start import");
		RestTemplate restTemplate = new RestTemplate();
		if (this.crudGenereRep.findAll().isEmpty()) {
			LOG.info("Start import generes");
			WrapperGenereDto result = restTemplate.getForObject(
					"https://api.themoviedb.org/3/genre/movie/list?api_key=" + user.getMoviedbkey() + "&language=en-US",
					WrapperGenereDto.class);
			for (GenereDto g : result.getGenres()) {
				Genere genereEntity = Converter.convert(g);
				this.crudGenereRep.save(genereEntity);
			}
		}
		LOG.info("Start import Movie");
		String queryStr = this.createQueryStr(title);
		WrapperMovieDto wrMovie = restTemplate
				.getForObject(
						"https://api.themoviedb.org/3/search/movie?api_key=" + user.getMoviedbkey()
								+ "&language=en-US&query=" + queryStr + "&page=1&include_adult=false",
						WrapperMovieDto.class);
		Movie movieEntity = this.customRep.findByMovieId(wrMovie.getResults()[number].getMovieId()).orElse(null);
		if (movieEntity == null) {
			LOG.info("Movie not found by id");
			List<Movie> movies = this.crudMovieRep.findByTitleAndRelDate(wrMovie.getResults()[number].getTitle(),
					wrMovie.getResults()[number].getReleaseDate(), this.getCurrentUser().getId());
			if (!movies.isEmpty()) {
				LOG.info("Movie found by Title and Reldate");
				movieEntity = movies.get(0);
				movieEntity.setMovieId(wrMovie.getResults()[number].getId());
			} else {
				LOG.info("creating new Movie");
				movieEntity = Converter.convert(wrMovie.getResults()[number]);
				for (int genId : wrMovie.getResults()[number].getGeneres()) {
					Optional<Genere> result = this.customRep.findByGenereId(genId);
					if (result.isPresent()) {
						movieEntity.getGeneres().add(result.get());
					}
				}
				this.crudMovieRep.save(movieEntity);
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
				Cast castEntity = Converter.convert(c);
				movieEntity.getCast().add(castEntity);
				castEntity.setMovie(movieEntity);
				ActorDto actor = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + c.getId()
						+ "?api_key=" + user.getMoviedbkey() + "&language=en-US", ActorDto.class);
				Optional<Actor> actorOpt = this.customRep.findByActorId(actor.getId().intValue());
				Actor actorEntity = actorOpt.isPresent() ? actorOpt.get() : Converter.convert(actor);
				actorEntity.getCasts().add(castEntity);
				castEntity.setActor(actorEntity);						
				this.crudCastRep.save(castEntity);
				if (!actorOpt.isPresent()) {
					actorEntity.getUsers().add(user);
					this.crudActorRep.save(actorEntity);
				}
				Thread.sleep(300);
			}
		} else {
			for (CastDto c : wrCast.getCast()) {
				LOG.info("update cast for movie");
				ActorDto actor = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + c.getId()
						+ "?api_key=" + user.getMoviedbkey() + "&language=en-US", ActorDto.class);
				Optional<Actor> actorOpt = this.customRep.findByActorId(actor.getId().intValue());
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

	private User getCurrentUser() {
		return this.crudUserRep
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
	}
}
