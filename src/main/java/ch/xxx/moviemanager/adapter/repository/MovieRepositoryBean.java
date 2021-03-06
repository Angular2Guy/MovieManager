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
package ch.xxx.moviemanager.adapter.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;

@Repository
public class MovieRepositoryBean implements MovieRepository {
	private final JpaMovieRepository jpaMovieRepository;

	public MovieRepositoryBean(JpaMovieRepository jpaMovieRepository) {
		this.jpaMovieRepository = jpaMovieRepository;
	}

	@Override
	public Optional<Movie> findById(Long id) {
		return this.jpaMovieRepository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		this.jpaMovieRepository.deleteById(id);
	}
	
	@Override
	public Movie save(Movie movieEntity) {
		return this.jpaMovieRepository.save(movieEntity);
	}
	
	@Override
	public List<Movie> findByTitle(String title, Long userId, Pageable pageable) {
		return this.jpaMovieRepository.findByTitle(title, userId, pageable);
	}

	@Override
	public List<Movie> findByGenereId(Long id, Long userId) {
		return this.jpaMovieRepository.findByGenereId(id, userId);
	}

	@Override
	public List<Movie> findByTitleAndRelDate(String title, Date releaseDate, Long userId) {
		return this.jpaMovieRepository.findByTitleAndRelDate(title, releaseDate, userId);
	}
	
	@Override	
	public Optional<Movie> findByMovieId(Long movieId, Long userId) {
		return this.jpaMovieRepository.findByMovieId(movieId, userId);
	}
	
	@Override
	public List<Movie> findMoviesByPage(Long userId, Pageable pageable) {
		return this.jpaMovieRepository.findMoviesByPage(userId, pageable);
	}

	@Override
	public List<Movie> findUnusedMovies() {
		return this.jpaMovieRepository.findUnusedMovies();
	}
	
	@Override
	public Optional<Movie> findByIdWithCollections(Long ids) {
		return this.jpaMovieRepository.findByIdWithCollections(ids);
	}
}
