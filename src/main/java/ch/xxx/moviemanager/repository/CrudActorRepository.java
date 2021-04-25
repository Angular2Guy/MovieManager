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

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ch.xxx.moviemanager.model.Actor;

public interface CrudActorRepository extends PagingAndSortingRepository<Actor,Long>{
	@Query("select a from Actor a join a.users u where lower(a.name) like lower(concat('%',:name,'%')) and u.id = :userid order by a.name")
	List<Actor> findByActorName(@Param("name") String name, @Param("userid") Long userId);
	
	@Query("select a from Actor a join a.users u where a.actorId = :actorId and u.id = :userId")
	Optional<Actor> findByActorId(Long actorId, Long userId);
	
	@Query("select a from Actor a join a.users u where u.id = :userId order by a.name")
	List<Actor> findActorsByPage(Long userId, Pageable pageble);
}
