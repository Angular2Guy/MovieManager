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
package ch.xxx.moviemanager.domain.model.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="cast1")
public class Cast extends EntityBase {
	@NotBlank
	@Size(max=255)
	private String movieChar;
	@NotBlank
	@Size(max=255)
	private String characterName;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="movie_id")
	private Movie movie;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="actor_id")
	private Actor actor;
	
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
