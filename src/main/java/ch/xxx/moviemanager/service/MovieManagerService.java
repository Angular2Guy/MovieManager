package ch.xxx.moviemanager.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
		List<Genere> generes = this.crudGenereRep.findAll();
		List<GenereDto> result = generes.stream().map(gen -> Converter.convert(gen)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMoviesByGenere(Long id) {
		List<Movie> movies = this.customRep.findByGenereId(id);
		List<MovieDto> result = movies.stream().map(m -> Converter.convert(m)).collect(Collectors.toList());
		return result;
	}

	public Optional<MovieDto> findMovieById(Long id) {
		Optional<Movie> result = this.crudMovieRep.findById(id);
		Optional<MovieDto> res = Optional.empty();
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
		List<Actor> actors = this.customRep.findByActorName(name);
		List<ActorDto> result = actors.stream().map(a -> Converter.convert(a)).collect(Collectors.toList());
		return result;
	}

	public List<MovieDto> findMovie(String title) {
		List<Movie> movies = this.customRep.findByTitle(title);
		List<MovieDto> result = movies.stream().map(m -> Converter.convert(m)).collect(Collectors.toList());
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
		RestTemplate restTemplate = new RestTemplate();
		if (this.crudGenereRep.findAll().isEmpty()) {
			WrapperGenereDto result = restTemplate.getForObject(
					"https://api.themoviedb.org/3/genre/movie/list?api_key=" + user.getMoviedbkey() + "&language=en-US",
					WrapperGenereDto.class);
			for (GenereDto g : result.getGenres()) {
				Genere genereEntity = Converter.convert(g);
				this.crudGenereRep.save(genereEntity);
			}
		}
		String queryStr = this.createQueryStr(title);
		WrapperMovieDto wrMovie = restTemplate
				.getForObject(
						"https://api.themoviedb.org/3/search/movie?api_key=" + user.getMoviedbkey()
								+ "&language=en-US&query=" + queryStr + "&page=1&include_adult=false",
						WrapperMovieDto.class);
		Movie movieEntity = this.customRep.findByMovieId(wrMovie.getResults()[number].getMovieId()).orElse(null);
		if (movieEntity == null) {
			List<Movie> movies = this.customRep.findByTitleAndRelDate(wrMovie.getResults()[number].getTitle(),
					wrMovie.getResults()[number].getReleaseDate());
			if (!movies.isEmpty()) {
				movieEntity = movies.get(0);
				movieEntity.setMovieid(wrMovie.getResults()[number].getId().intValue());
			}
		} else {
			movieEntity = Converter.convert(wrMovie.getResults()[number]);
			for (int genId : wrMovie.getResults()[number].getGeneres()) {
				Optional<Genere> result = this.customRep.findByGenereId(genId);
				if (result.isPresent()) {
					movieEntity.getGeneres().add(result.get());
				}
			}
			this.crudMovieRep.save(movieEntity);
		}
		if (!movieEntity.getUsers().contains(user)) {
			movieEntity.getUsers().add(user);
		}
		WrapperCastDto wrCast = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"
				+ wrMovie.getResults()[number].getId() + "/credits?api_key=" + user.getMoviedbkey(),
				WrapperCastDto.class);
		if (movieEntity.getCast().isEmpty()) {
			for (CastDto c : wrCast.getCast()) {
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
					this.crudActorRep.save(actorEntity);
				}
				Thread.sleep(300);
			}
		} else {
			for (CastDto c : wrCast.getCast()) {
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

//	public boolean updateDB() {
//		try {
//			User user = getCurrentUser();
//			this.crudMovieRep.findAll().forEach(m -> {
//				if (m.getUsers().isEmpty()) {
//					m.getUsers().add(user);
//				}
//			});
//			this.crudActorRep.findAll().forEach(a -> {
//				if (a.getUsers().isEmpty()) {
//					a.getUsers().add(user);
//				}
//			});
//		} catch (RuntimeException e) {
//			return false;
//		}
//		return true;
//	}

	private String createQueryStr(String str) {
		return str.replace(" ", "%20");
	}

	private User getCurrentUser() {
		return this.crudUserRep
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
	}
}
