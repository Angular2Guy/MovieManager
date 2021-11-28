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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Movie extends EntityBase {
	@Column(columnDefinition = "clob")
	private String overview;
	private Date releaseDate;
	private String title;
	private Long movieId;
	@OneToMany(mappedBy="movie", cascade = CascadeType.ALL, orphanRemoval=true)	
	private List<Cast> cast = new ArrayList<>();
	@ManyToMany
	@JoinTable(name = "movie_genere", 
		joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genere_id"))
	private Set<Genere> generes = new HashSet<>();
	@ManyToMany
	@JoinTable(name = "movie_user", 
		joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users = new HashSet<>();
	
	
	public Set<User> getUsers() {
		return users;
	}
	public Long getMovieId() {
		return movieId;
	}
	public void setMovieId(Long movieid) {
		this.movieId = movieid;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Cast> getCast() {
		return cast;
	}
	public void setCast(List<Cast> cast) {
		this.cast = cast;
	}
	public Set<Genere> getGeneres() {
		return generes;
	}
	public void setGeneres(Set<Genere> generes) {
		this.generes = generes;
	}
	
}
