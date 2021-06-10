package ch.xxx.moviemanager.adapter.repository;

import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.Cast;
import ch.xxx.moviemanager.domain.model.CastRepository;

@Repository
public class CastRepositoryBean implements CastRepository {
	private final JpaCastRepository jpaCastRepository;
	
	public CastRepositoryBean(JpaCastRepository jpaCastRepository) {
		this.jpaCastRepository = jpaCastRepository;
	}

	public Cast save(Cast cast) {
		return this.jpaCastRepository.save(cast);
	}
}
