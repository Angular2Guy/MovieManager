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
package ch.xxx.moviemanager.adapter.client;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;

@Service
public class MovieDbRestClientBean implements MovieDbRestClient {
	private final static Logger LOG = LoggerFactory.getLogger(MovieDbRestClientBean.class);
	private final ObjectMapper objectMapper;

	public MovieDbRestClientBean(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public WrapperGenereDto fetchAllGeneres(String moviedbkey) {
		WrapperGenereDto result = RestClient
				.create().get().uri(URI.create(String
						.format("https://api.themoviedb.org/3/genre/movie/list?api_key=%s&language=en-US", moviedbkey)))
				.retrieve().body(WrapperGenereDto.class);
		return result;
	}

	public MovieDto fetchMovie(String moviedbkey, long movieDbId) {
		MovieDto wrMovie = RestClient.create().get().uri(URI.create(String.format("https://api.themoviedb.org/3/movie/%d?api_key=%s&language=en-US",
						movieDbId, moviedbkey))).retrieve().body(MovieDto.class);
		return wrMovie;
	}

	private <T> T parseJsonToDto(String bodyStr, Class<T> result) {
		LOG.info(bodyStr);
		try {
			return this.objectMapper.readValue(bodyStr, result);
		} catch (JacksonException e) {
			throw new RuntimeException("Failed to parse movie json.", e);
		}
	}

	public WrapperCastDto fetchCast(String moviedbkey, Long movieId, Long delay) {
		waitFor(delay);
		WrapperCastDto wrCast = RestClient.create().get()
				.uri(URI.create(
						String.format("https://api.themoviedb.org/3/movie/%d/credits?api_key=%s", movieId, moviedbkey)))
				.retrieve().body(WrapperCastDto.class);
		return wrCast;
	}

	private void waitFor(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		}
	}

	public ActorDto fetchActor(String moviedbkey, Integer castId, Long delay) {
		this.waitFor(delay);
		ActorDto actor = RestClient.create().get().uri(URI.create(
				String.format("https://api.themoviedb.org/3/person/%d?api_key=%s&language=en-US", castId, moviedbkey)))
				.retrieve().body(ActorDto.class);
//				.bodyToMono(String.class)
//				.map(bodyStr -> {
//					return parseJsonToDto(bodyStr, ActorDto.class);
//				})				
		return actor;
	}

	public ActorDto fetchActor(String moviedbkey, Integer castId) {
		return this.fetchActor(moviedbkey, castId, 0L);
	}

	public WrapperMovieDto fetchImportMovie(String moviedbkey, String queryStr) {
		WrapperMovieDto wrMovie = RestClient.create().get().uri(URI.create(String.format(
				"https://api.themoviedb.org/3/search/movie?api_key=%s&language=en-US&query=%s&page=1&include_adult=false",
				moviedbkey, queryStr))).retrieve().body(WrapperMovieDto.class);
		MovieDto[] movieArray = Arrays.stream(wrMovie.getResults()).map(movieDto -> {
			movieDto.setMovieId(movieDto.getId());
			movieDto.setId(null);
			return movieDto;
		}).toArray(MovieDto[]::new);
		wrMovie.setResults(movieArray);
		return wrMovie;
	}
}
