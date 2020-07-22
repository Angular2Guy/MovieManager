package ch.xxx.moviemanager.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.xxx.moviemanager.model.Movie;

public interface CrudMovieRepository extends JpaRepository<Movie,Long>{

	@Query("select e from Movie e join e.users u where lower(e.title) like lower(concat('%',:title,'%')) and u.id = :userid order by e.title")
	List<Movie> findByTitle(@Param("title") String title, @Param("userid") Long userid);
	
	@Query("select m from Movie m join m.generes g join m.users u where g.genereId = :id and u.id = :userid order by m.title")
	List<Movie> findByGenereId(@Param("id") Long id, @Param("userid") Long userid);
	
	@Query("select e from Movie e join e.users u where lower(e.title) like lower(concat('%',:title,'%')) and e.releaseDate = :relDate and u.id = :userid order by e.title")
	List<Movie> findByTitleAndRelDate(@Param("title") String title, @Param("relDate") Date releaseDate, @Param("userid") Long userid);
}
