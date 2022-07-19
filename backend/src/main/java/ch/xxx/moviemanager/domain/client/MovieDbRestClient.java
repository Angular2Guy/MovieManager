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
package ch.xxx.moviemanager.domain.client;

import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperCastDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperGenereDto;
import ch.xxx.moviemanager.domain.model.dto.WrapperMovieDto;

public interface MovieDbRestClient {
	MovieDto fetchMovie(String moviedbkey, int movieDbId);
	
	WrapperCastDto fetchCast(String moviedbkey, Long movieId);
	
	ActorDto fetchActor(String moviedbkey, Integer castId);
	
	ActorDto fetchActor(String moviedbkey, Integer castId, Long delay);
	
	WrapperGenereDto fetchAllGeneres(String moviedbkey);
	
	WrapperMovieDto fetchImportMovie(String moviedbkey, String queryStr);
}
