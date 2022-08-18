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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.xxx.moviemanager.domain.model.dto.ActorDto.Gender;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorFilterCriteriaDto {
    private String name;
    private Gender gender = Gender.Unknown;
    private LocalDate birthdayFrom;
    private LocalDate birthdayTo;
    private Boolean dead;
    private int popularity;
    private String movieCharacter;
    private SearchPhraseDto searchPhrase = new SearchPhraseDto();
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public LocalDate getBirthdayFrom() {
		return birthdayFrom;
	}
	public void setBirthdayFrom(LocalDate birthdayFrom) {
		this.birthdayFrom = birthdayFrom;
	}
	public LocalDate getBirthdayTo() {
		return birthdayTo;
	}
	public void setBirthdayTo(LocalDate birthdayTo) {
		this.birthdayTo = birthdayTo;
	}
	public Boolean getDead() {
		return dead;
	}
	public void setDead(Boolean dead) {
		this.dead = dead;
	}
	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public SearchPhraseDto getSearchPhrase() {
		return searchPhrase;
	}
	public void setSearchPhrase(SearchPhraseDto searchPhrase) {
		this.searchPhrase = searchPhrase;
	}
	public String getMovieCharacter() {
		return movieCharacter;
	}
	public void setMovieCharacter(String movieCharacter) {
		this.movieCharacter = movieCharacter;
	}
}
