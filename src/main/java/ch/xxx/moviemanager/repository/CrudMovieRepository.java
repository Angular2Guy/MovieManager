package ch.xxx.moviemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.xxx.moviemanager.model.Movie;

public interface CrudMovieRepository extends JpaRepository<Movie,Long>{

}
