package ch.xxx.moviemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.xxx.moviemanager.model.Genere;

public interface CrudGenereRepository extends JpaRepository<Genere,Long> {

}
