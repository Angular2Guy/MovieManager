package ch.xxx.moviemanager.adapter.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.Movie;
import ch.xxx.moviemanager.domain.model.MovieRepository;

@Repository
public class MovieRepositoryBean implements MovieRepository {
	private final JpaMovieRepository jpaMovieRepository;

	public MovieRepositoryBean(JpaMovieRepository jpaMovieRepository) {
		this.jpaMovieRepository = jpaMovieRepository;
	}

	public Optional<Movie> findById(Long id) {
		return this.jpaMovieRepository.findById(id);
	}

	public void deleteById(Long id) {
		this.jpaMovieRepository.deleteById(id);
	}
	
	public Movie save(Movie movieEntity) {
		return this.jpaMovieRepository.save(movieEntity);
	}
	
	public List<Movie> findByTitle(String title, Long userId) {
		return this.jpaMovieRepository.findByTitle(title, userId);
	}

	public List<Movie> findByGenereId(Long id, Long userId) {
		return this.jpaMovieRepository.findByGenereId(id, userId);
	}

	public List<Movie> findByTitleAndRelDate(String title, Date releaseDate, Long userId) {
		return this.jpaMovieRepository.findByTitleAndRelDate(title, releaseDate, userId);
	}

	public Optional<Movie> findByMovieId(Long movieId, Long userId) {
		return this.jpaMovieRepository.findByMovieId(movieId, userId);
	}

	public List<Movie> findMoviesByPage(Long userId, Pageable pageable) {
		return this.jpaMovieRepository.findMoviesByPage(userId, pageable);
	}
}
