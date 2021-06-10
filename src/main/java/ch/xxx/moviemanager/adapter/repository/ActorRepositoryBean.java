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
