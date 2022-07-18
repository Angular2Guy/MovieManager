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
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;

@Service
public class MovieDbRestClientBean implements MovieDbRestClient {

	public WrapperGenereDto fetchAllGeneres(String moviedbkey) {
		WrapperGenereDto result = WebClient.create().get()
			.uri(URI.create("https://api.themoviedb.org/3/genre/movie/list?api_key=" + moviedbkey + "&language=en-US"))
			.retrieve().bodyToMono(WrapperGenereDto.class).block(Duration.ofSeconds(10L));
		return result;
	}

	public WrapperMovieDto fetchMovie(String moviedbkey, String queryStr) {
		WrapperMovieDto wrMovie = WebClient.create().get()
		.uri(URI.create("https://api.themoviedb.org/3/search/movie?api_key="
				+ moviedbkey + "&language=en-US&query=" + queryStr + "&page=1&include_adult=false"))
		.retrieve().bodyToMono(WrapperMovieDto.class).block(Duration.ofSeconds(10L));
		return wrMovie;
	}

	public WrapperCastDto fetchCast(String moviedbkey, Long movieId) {
		WrapperCastDto wrCast = WebClient.create().get()
		.uri(URI.create("https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + moviedbkey))
		.retrieve().bodyToMono(WrapperCastDto.class).block(Duration.ofSeconds(10L));
		return wrCast;
	}

	public ActorDto fetchActor(String moviedbkey, Integer castId) {
		ActorDto actor = WebClient.create().get()
		.uri(URI.create("https://api.themoviedb.org/3/person/" + castId + "?api_key=" + moviedbkey + "&language=en-US"))
		.retrieve().bodyToMono(ActorDto.class).block(Duration.ofSeconds(10L));
		return actor;
	}

	public WrapperMovieDto fetchImportMovie(String moviedbkey, String queryStr) {
		WrapperMovieDto wrMovie = WebClient.create().get()
		.uri(URI.create("https://api.themoviedb.org/3/search/movie?api_key="
				+ moviedbkey + "&language=en-US&query=" + queryStr + "&page=1&include_adult=false"))
		.retrieve().bodyToMono(WrapperMovieDto.class).block(Duration.ofSeconds(10L));
		return wrMovie;
	}
}
