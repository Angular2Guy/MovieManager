package ch.xxx.moviemanager.adapter.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;

@Service
public class MovieDbRestClientBean implements MovieDbRestClient {
	
	public WrapperGenereDto fetchAllGeneres(String moviedbkey) {
		RestTemplate restTemplate = new RestTemplate();
		WrapperGenereDto result = restTemplate.getForObject(
				"https://api.themoviedb.org/3/genre/movie/list?api_key=" + moviedbkey + "&language=en-US",
				WrapperGenereDto.class);
		return result;
	}
	
	public WrapperMovieDto fetchMovie(String moviedbkey, String queryStr) {
		RestTemplate restTemplate = new RestTemplate();
		WrapperMovieDto wrMovie = restTemplate
				.getForObject(
						"https://api.themoviedb.org/3/search/movie?api_key=" + moviedbkey
								+ "&language=en-US&query=" + queryStr + "&page=1&include_adult=false",
						WrapperMovieDto.class);
		return wrMovie;
	}
	
	public WrapperCastDto fetchCast(String moviedbkey, Long movieId) {
		RestTemplate restTemplate = new RestTemplate();
		WrapperCastDto wrCast = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"
				+ movieId + "/credits?api_key=" + moviedbkey,
				WrapperCastDto.class);
		return wrCast;
	}
	
	public ActorDto fetchActor(String moviedbkey, Integer castId) {
		RestTemplate restTemplate = new RestTemplate();
		ActorDto actor = restTemplate.getForObject("https://api.themoviedb.org/3/person/" + castId
		+ "?api_key=" + moviedbkey + "&language=en-US", ActorDto.class);
		return actor;
	}
}
