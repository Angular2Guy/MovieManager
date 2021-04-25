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
package ch.xxx.moviemanager.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.xxx.moviemanager.model.Movie;
import ch.xxx.moviemanager.model.User;

public interface CrudMovieRepository extends JpaRepository<Movie,Long>{

	@Query("select e from Movie e join e.users u where lower(e.title) like lower(concat('%',:title,'%')) and u.id = :userid order by e.title")
	List<Movie> findByTitle(@Param("title") String title, @Param("userid") Long userid);
	
	@Query("select m from Movie m join m.generes g join m.users u where g.genereId = :id and u.id = :userid order by m.title")
	List<Movie> findByGenereId(@Param("id") Long id, @Param("userid") Long userid);
	
	@Query("select e from Movie e join e.users u where lower(e.title) like lower(concat('%',:title,'%')) and e.releaseDate = :relDate and u.id = :userid order by e.title")
	List<Movie> findByTitleAndRelDate(@Param("title") String title, @Param("relDate") Date releaseDate, @Param("userid") Long userid);
	
	@Query("select m from Movie m join m.users u where m.movieId = :movieid and u.id = :userId")
	Optional<Movie> findByMovieId(Long movieId, Long userId);
}
