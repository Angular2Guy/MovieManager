package ch.xxx.moviemanager.service;

import ch.xxx.moviemanager.dto.ActorDto;
import ch.xxx.moviemanager.dto.CastDto;
import ch.xxx.moviemanager.dto.GenereDto;
import ch.xxx.moviemanager.dto.MovieDto;
import ch.xxx.moviemanager.model.Actor;
import ch.xxx.moviemanager.model.Cast;
import ch.xxx.moviemanager.model.Genere;
import ch.xxx.moviemanager.model.Movie;

public class Converter {
	public static MovieDto convert(Movie entity) {
		MovieDto dto = convertMovie(entity);
		entity.getCast().forEach(c -> {
			CastDto castDto = convert(c, true); 
			dto.getMyCast().add(castDto);
		});		
		return dto;
	}
	
	public static ActorDto convert(Actor entity) {
		ActorDto dto = convertActor(entity);
		entity.getCasts().forEach(c -> {
			CastDto castDto = convert(c, false);
			dto.getMyCasts().add(castDto);			
		});
		return dto;
	}
	
	public static GenereDto convert(Genere entity) {
		GenereDto dto = new GenereDto();
		dto.setId(entity.getGenereId());
		dto.setName(entity.getName());
		return dto;
	}
	
	private static CastDto convert(Cast entity, boolean fromMovie) {
		CastDto dto = new CastDto();
		dto.setCharacter(entity.getMovieChar());
		dto.setName(entity.getCharacterName());
		if(fromMovie) 
			dto.setMyActor(convertActor(entity.getActor()));
		else 
			dto.setMyMovie(convertMovie(entity.getMovie()));
		return dto;
	}
	
	private static MovieDto convertMovie(Movie entity) {
		MovieDto dto = new MovieDto();
		dto.setId(entity.getId());
		dto.setOverview(entity.getOverview());
		dto.setReleaseDate(entity.getReleaseDate());
		dto.setTitle(entity.getTitle());
		dto.setMovieId(entity.getMovieid());
		entity.getGeneres().forEach(g -> {
			GenereDto genereDto = convert(g);
			dto.getMyGenere().add(genereDto);
		});
		return dto;
	}
	
	private static ActorDto convertActor(Actor entity) {
		ActorDto dto = new ActorDto();
		dto.setBiography(entity.getBiography());
		dto.setBirthday(entity.getBirthday());
		dto.setDeathday(entity.getDeathday());
		dto.setGender(entity.getGender());
		dto.setName(entity.getName());
		dto.setPlaceOfBirth(entity.getPlaceOfBirth());
		dto.setId(entity.getId());
		dto.setActorId(entity.getActorId());
		return dto;
	}
	
	public static Genere convert(GenereDto dto) {
		Genere entity = new Genere();
		entity.setName(dto.getName());
		entity.setGenereId(dto.getId());
		return entity;
	}
	
	public static Movie convert(MovieDto dto) {
		Movie entity = new Movie();
		entity.setOverview(dto.getOverview());
		entity.setReleaseDate(dto.getReleaseDate());
		entity.setTitle(dto.getTitle());
		entity.setMovieid(dto.getId().intValue());
		return entity;
	}
	
	public static Cast convert(CastDto dto) {
		Cast entity = new Cast();
		entity.setCharacterName(dto.getName());
		entity.setMovieChar(dto.getCharacter());
		return entity;
	}
	
	public static Actor convert(ActorDto dto) {
		Actor entity = new Actor();
		entity.setActorId(dto.getActorId() == 0 ? dto.getId().intValue() : dto.getActorId());
		entity.setBiography(dto.getBiography());
		entity.setBirthday(dto.getBirthday());
		entity.setDeathday(dto.getDeathday());
		entity.setGender(dto.getGender());
		entity.setName(dto.getName());
		entity.setPlaceOfBirth(dto.getPlaceOfBirth());
		return entity;
	}
}
