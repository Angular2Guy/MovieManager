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

import java.time.LocalDate;
import java.util.List;

public class FilterCriteriaDto {
	private List<GenereDto> selectedGeneres;
	private LocalDate releaseFrom;
	private LocalDate releaseTo;
	private String movieTitle;
	private String movieActor;
	private int minLength;
	private int maxLength;
	private int minRating;
	private SearchPhraseDto searchPraseDto = new SearchPhraseDto();
	
	public List<GenereDto> getSelectedGeneres() {
		return selectedGeneres;
	}
	public void setSelectedGeneres(List<GenereDto> selectedGeneres) {
		this.selectedGeneres = selectedGeneres;
	}
	public LocalDate getReleaseFrom() {
		return releaseFrom;
	}
	public void setReleaseFrom(LocalDate releaseFrom) {
		this.releaseFrom = releaseFrom;
	}
	public LocalDate getReleaseTo() {
		return releaseTo;
	}
	public void setReleaseTo(LocalDate releaseTo) {
		this.releaseTo = releaseTo;
	}
	public String getMovieTitle() {
		return movieTitle;
	}
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	public String getMovieActor() {
		return movieActor;
	}
	public void setMovieActor(String movieActor) {
		this.movieActor = movieActor;
	}
	public int getMinLength() {
		return minLength;
	}
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public int getMinRating() {
		return minRating;
	}
	public void setMinRating(int minRating) {
		this.minRating = minRating;
	}
	public SearchPhraseDto getSearchPraseDto() {
		return searchPraseDto;
	}
	public void setSearchPraseDto(SearchPhraseDto searchPraseDto) {
		this.searchPraseDto = searchPraseDto == null ? searchPraseDto : this.searchPraseDto;
	}
}
