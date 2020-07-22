package ch.xxx.moviemanager.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Genere {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	private Long genereId;
	private String name;
	@ManyToMany(mappedBy="generes")
	private List<Movie> movies = new ArrayList<>();
	
	public Long getGenereId() {
		return genereId;
	}
	public void setGenereId(Long genereId) {
		this.genereId = genereId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Movie> getMovies() {
		return movies;
	}
	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
	
}
