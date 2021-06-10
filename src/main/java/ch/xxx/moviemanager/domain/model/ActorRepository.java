package ch.xxx.moviemanager.domain.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

public interface ActorRepository {
	List<Actor> findActorsByPage(Long userId, Pageable pageble);
	List<Actor> findByActorName(String name, Long userId);
	Optional<Actor> findByActorId(Long actorId, Long userId);
	void deleteById(Long id);
	Optional<Actor> findById(Long id);
	Actor save(Actor actorEntity);
}
