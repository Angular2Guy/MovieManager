package ch.xxx.moviemanager.domain.model;

import java.util.List;

public interface GenereRepository {	
	public List<Genere> findAll();
	public Genere save(Genere genere);
}
