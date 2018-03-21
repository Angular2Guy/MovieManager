package ch.xxx.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrapperGenereDto {
	private GenereDto[] genres;

	public GenereDto[] getGenres() {
		return genres;
	}

	public void setGenres(GenereDto[] genres) {
		this.genres = genres;
	}

	
}
