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

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.Actor;
import ch.xxx.moviemanager.domain.model.ActorRepository;

@Repository
public class ActorRepositoryBean implements ActorRepository {
	private final JpaActorRepository jpaActorRepository;
	
	public ActorRepositoryBean(JpaActorRepository jpaActorRepository) {
		this.jpaActorRepository = jpaActorRepository;
	}

	public Optional<Actor> findById(Long id) {
		return this.jpaActorRepository.findById(id);
	}
	
	public Actor save(Actor actorEntity) {
		return this.jpaActorRepository.save(actorEntity);
	}
	
	public void deleteById(Long id) {
		this.jpaActorRepository.deleteById(id);
	}
	
	public List<Actor> findByActorName(String name, Long userId) {
		return this.jpaActorRepository.findByActorName(name, userId);
	}
	
	public Optional<Actor> findByActorId(Long actorId, Long userId) {
		return this.jpaActorRepository.findByActorId(actorId, userId);
	}
	
	public List<Actor> findActorsByPage(Long userId, Pageable pageble) {
		return this.findActorsByPage(userId, pageble);
	}
}
