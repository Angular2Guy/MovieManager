package ch.xxx.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrapperCastDto {
	private int id;
	private CastDto[] cast;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public CastDto[] getCast() {
		return cast;
	}
	public void setCast(CastDto[] cast) {
		this.cast = cast;
	}
	
}
