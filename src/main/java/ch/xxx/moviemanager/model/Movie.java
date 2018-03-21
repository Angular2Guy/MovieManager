package ch.xxx.moviemanager.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String overview;
	@Column(name="releasedate")
	private Date releaseDate;
	private String title;
	@Column(name="movieid")
	private Integer movieid;
	@OneToMany(mappedBy="movie", cascade = CascadeType.ALL, orphanRemoval=true)	
	private List<Cast> cast = new ArrayList<>();
	@ManyToMany
	@JoinTable(name = "Movie_Genere", 
		joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genere_id"))
	private List<Genere> generes = new ArrayList<>();
	@ManyToMany
	@JoinTable(name = "Movie_User", 
		joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users = new ArrayList<>();
	
	
	public List<User> getUsers() {
		return users;
	}
	public Integer getMovieid() {
		return movieid;
	}
	public void setMovieid(Integer movieid) {
		this.movieid = movieid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public List<Genere> getGeneres() {
		return generes;
	}
	public void setGeneres(List<Genere> generes) {
		this.generes = generes;
	}
	
}
