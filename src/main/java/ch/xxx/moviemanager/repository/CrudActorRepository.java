package ch.xxx.moviemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.xxx.moviemanager.model.Actor;

public interface CrudActorRepository extends JpaRepository<Actor,Long>{
	@Query("select a from Actor a join a.users u where lower(a.name) like lower(concat('%',:name,'%')) and u.id = :userid order by a.name")
	List<Actor> findByActorName(@Param("name") String name, @Param("userid") Long userId);
}
