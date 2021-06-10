package ch.xxx.moviemanager.adapter.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.Genere;
import ch.xxx.moviemanager.domain.model.GenereRepository;

@Repository
public class GenereRepositoryBean implements GenereRepository {
	private final JpaGenereRepository jpaGenereRepository;
	
	public GenereRepositoryBean(JpaGenereRepository jpaGenereRepository) {
		this.jpaGenereRepository = jpaGenereRepository;
	}
	
	public List<Genere> findAll() {
		return this.jpaGenereRepository.findAll();
	}
	
	public Genere save(Genere genere) {
		return this.save(genere);
	}
}
