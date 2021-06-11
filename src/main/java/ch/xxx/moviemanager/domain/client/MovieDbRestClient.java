package ch.xxx.moviemanager.domain.client;

import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;

public interface MovieDbRestClient {
	WrapperMovieDto fetchMovie(String moviedbkey, String queryStr);
	
	WrapperCastDto fetchCast(String moviedbkey, Long movieId);
	
	ActorDto fetchActor(String moviedbkey, Integer castId);
	
	WrapperGenereDto fetchAllGeneres(String moviedbkey);
}
