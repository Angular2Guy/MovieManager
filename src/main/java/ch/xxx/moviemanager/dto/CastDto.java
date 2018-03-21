package ch.xxx.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CastDto {
	private int id;
	private String character;
	private String name;
	private MovieDto myMovie;
	private ActorDto myActor;
	
	public ActorDto getMyActor() {
		return myActor;
	}
	public void setMyActor(ActorDto myActor) {
		this.myActor = myActor;
	}
	public MovieDto getMyMovie() {
		return myMovie;
	}
	public void setMyMovie(MovieDto myMovie) {
		this.myMovie = myMovie;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCharacter() {
		return character;
	}
	public void setCharacter(String character) {
		this.character = character;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
