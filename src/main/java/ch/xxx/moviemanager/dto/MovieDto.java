package ch.xxx.moviemanager.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDto {
	private Long id;
	private String overview;
	@JsonProperty("release_date")
	private Date releaseDate;
	private String title;	
	private Integer movieId;
	@JsonProperty("genre_ids")
	private int[] generes;
	private List<CastDto> myCast = new ArrayList<>();
	private List<GenereDto> myGenere = new ArrayList<>();
	
	public Integer getMovieId() {
		return movieId;
	}
	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}
	public List<CastDto> getMyCast() {
		return myCast;
	}
	public List<GenereDto> getMyGenere() {
		return myGenere;
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
	public int[] getGeneres() {
		return generes;
	}
	public void setGeneres(int[] generes) {
		this.generes = generes;
	}
	public void setMyCast(List<CastDto> myCast) {
		this.myCast = myCast;
	}
	public void setMyGenere(List<GenereDto> myGenere) {
		this.myGenere = myGenere;
	}	
}
