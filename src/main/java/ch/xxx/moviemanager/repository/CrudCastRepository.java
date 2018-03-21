package ch.xxx.moviemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.xxx.moviemanager.model.Cast;

public interface CrudCastRepository extends JpaRepository<Cast,Long>{

}
