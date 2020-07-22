package ch.xxx.moviemanager.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="cast1")
public class Cast {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	private String movieChar;
	private String characterName;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="movie_id")
	private Movie movie;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="actor_id")
	private Actor actor;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMovieChar() {
		return movieChar;
	}
	public void setMovieChar(String movieChar) {
		this.movieChar = movieChar;
	}
	public String getCharacterName() {
		return characterName;
	}
	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}
	public Movie getMovie() {
		return movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	public Actor getActor() {
		return actor;
	}
	public void setActor(Actor actor) {
		this.actor = actor;
	}
	
}
