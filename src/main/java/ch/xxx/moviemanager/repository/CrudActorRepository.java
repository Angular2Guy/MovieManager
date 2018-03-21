package ch.xxx.moviemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.xxx.moviemanager.model.Actor;

public interface CrudActorRepository extends JpaRepository<Actor,Long>{

}
