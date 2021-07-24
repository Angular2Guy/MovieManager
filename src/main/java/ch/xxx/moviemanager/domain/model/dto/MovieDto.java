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
package ch.xxx.moviemanager.domain.model.dto;

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
	private Long movieId;
	@JsonProperty("genre_ids")
	private int[] generes;
	private List<CastDto> myCast = new ArrayList<>();
	private List<GenereDto> myGenere = new ArrayList<>();
	
	public Long getMovieId() {
		return movieId;
	}
	public void setMovieId(Long movieId) {
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
