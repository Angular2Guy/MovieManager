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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ch.xxx.moviemanager.domain.model.entity.Movie;

public interface JpaMovieRepository extends PagingAndSortingRepository<Movie,Long>, CrudRepository<Movie, Long>{
	
	@Query("select distinct m from Movie m join fetch m.cast c join fetch c.actor a join fetch m.generes where m.id = :id")
	Optional<Movie> findByIdWithCollections(Long id);

	@Query("select e from Movie e join e.users u where lower(e.title) like lower(concat('%',:title,'%')) and u.id = :userid")
	List<Movie> findByTitle(@Param("title") String title, @Param("userid") Long userId, Pageable pageable);
	
	@Query("select m from Movie m join m.generes g join m.users u where g.genereId = :id and u.id = :userid order by m.title")
	List<Movie> findByGenereId(@Param("id") Long id, @Param("userid") Long userId);
	
	@Query("select e from Movie e join e.users u where lower(e.title) like lower(concat('%',:title,'%')) and e.releaseDate = :relDate and u.id = :userid order by e.title")
	List<Movie> findByTitleAndRelDate(@Param("title") String title, @Param("relDate") Date releaseDate, @Param("userid") Long userId);
	
	@Query("select m from Movie m join m.users u join fetch m.cast where m.movieId = :movieId and u.id = :userId")
	Optional<Movie> findByMovieId(Long movieId, Long userId);
	
	@Query("select m from Movie m join m.users u where u.id = :userId order by m.title")	
	List<Movie> findMoviesByPage(Long userId, Pageable pageable);
	
	@Query("select m from Movie m where m.users is empty")
	List<Movie> findUnusedMovies();
}
