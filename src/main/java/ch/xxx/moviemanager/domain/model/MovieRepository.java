package ch.xxx.moviemanager.domain.model;

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
}
