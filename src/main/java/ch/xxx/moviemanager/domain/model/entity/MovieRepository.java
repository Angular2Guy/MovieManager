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
package ch.xxx.moviemanager.domain.model.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

public interface MovieRepository {
	List<Movie> findByTitle(String title, Long userId);
	List<Movie> findByGenereId(Long id, Long userId);
	List<Movie> findByTitleAndRelDate(String title, Date releaseDate, Long userId);
	Optional<Movie> findByMovieId(Long movieId, Long userId);
	List<Movie> findMoviesByPage(Long userId, Pageable pageable);
	Optional<Movie> findById(Long id);
	void deleteById(Long id);
	Movie save(Movie movieEntity);
	List<Movie> findUnusedMovies();
	List<Movie> findByIdsWithCollections(List<Long> ids);
}
