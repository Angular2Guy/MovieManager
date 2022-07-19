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
package ch.xxx.moviemanager.usecase.mapper;

import org.springframework.stereotype.Service;

import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.CastDto;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.Genere;
import ch.xxx.moviemanager.domain.model.entity.Movie;

@Service
public class DefaultMapper {

	public MovieDto convertMovieWithGenere(Movie entity) {
		MovieDto dto = convertMovie(entity, false);
		return dto;
	}

	public MovieDto convertOnlyMovie(Movie entity) {
		MovieDto dto = convertMovie(entity, true);
		return dto;
	}
	
	public MovieDto convert(Movie entity) {
		MovieDto dto = convertMovie(entity, false);
		entity.getCast().forEach(c -> {
			CastDto castDto = convert(c, true);
			dto.getMyCast().add(castDto);
		});
		return dto;
	}

	public ActorDto convertOnlyActor(Actor entity) {
		ActorDto dto = convertActor(entity);
		return dto;
	}
	
	public ActorDto convert(Actor entity) {
		ActorDto dto = convertActor(entity);
		entity.getCasts().forEach(c -> {
			CastDto castDto = convert(c, false);
			dto.getMyCasts().add(castDto);
		});
		return dto;
	}

	public GenereDto convert(Genere entity) {
		GenereDto dto = new GenereDto();
		dto.setId(entity.getGenereId());
		dto.setName(entity.getName());
		return dto;
	}

	private CastDto convert(Cast entity, boolean fromMovie) {
		CastDto dto = new CastDto();
		dto.setCharacter(entity.getMovieChar());
		dto.setName(entity.getCharacterName());
		if (fromMovie)
			dto.setMyActor(convertActor(entity.getActor()));
		else
			dto.setMyMovie(convertMovie(entity.getMovie(), false));
		return dto;
	}

	private MovieDto convertMovie(Movie entity, boolean noGeneres) {
		MovieDto dto = new MovieDto();
		dto.setId(entity.getId());
		dto.setOverview(entity.getOverview());
		dto.setReleaseDate(entity.getReleaseDate());
		dto.setTitle(entity.getTitle());
		dto.setRuntime(entity.getRuntime());
		dto.setRevenue(entity.getRevenue());
		dto.setVoteAverage(entity.getVoteAverage());
		dto.setVoteCount(entity.getVoteCount());
		dto.setBudget(entity.getBudget());
		dto.setMovieId(entity.getMovieId());
		if (!noGeneres) {
			entity.getGeneres().forEach(g -> {
				GenereDto genereDto = convert(g);
				dto.getMyGenere().add(genereDto);
			});
		}
		return dto;
	}

	private ActorDto convertActor(Actor entity) {
		ActorDto dto = new ActorDto();
		dto.setBiography(entity.getBiography());
		dto.setBirthday(entity.getBirthday());
		dto.setDeathday(entity.getDeathday());
		dto.setGender(entity.getGender());
		dto.setName(entity.getName());
		dto.setPlaceOfBirth(entity.getPlaceOfBirth());
		dto.setPopularity(entity.getPopularity());
		dto.setId(entity.getId());
		dto.setActorId(entity.getActorId());
		return dto;
	}

	public Genere convert(GenereDto dto) {
		Genere entity = new Genere();
		entity.setName(dto.getName());
		entity.setGenereId(dto.getId());
		return entity;
	}

	public Movie convert(MovieDto dto) {
		Movie entity = new Movie();
		entity.setOverview(dto.getOverview());
		entity.setReleaseDate(dto.getReleaseDate());
		entity.setTitle(dto.getTitle());
		entity.setRuntime(dto.getRuntime());
		entity.setRevenue(dto.getRevenue());
		entity.setVoteAverage(dto.getVoteAverage());
		entity.setVoteCount(dto.getVoteCount());
		entity.setBudget(dto.getBudget());
		entity.setMovieId(dto.getMovieId());
		return entity;
	}

	public Cast convert(CastDto dto) {
		Cast entity = new Cast();
		entity.setCharacterName(dto.getName());
		entity.setMovieChar(dto.getCharacter());
		return entity;
	}

	public Actor convert(ActorDto dto) {
		Actor entity = new Actor();
		entity.setActorId(
				dto.getActorId() == null || dto.getActorId() == 0 ? dto.getId().intValue() : dto.getActorId());
		entity.setBiography(dto.getBiography());
		entity.setBirthday(dto.getBirthday());
		entity.setDeathday(dto.getDeathday());
		entity.setGender(dto.getGender());
		entity.setName(dto.getName());
		entity.setPlaceOfBirth(dto.getPlaceOfBirth());
		entity.setPopularity(dto.getPopularity());
		return entity;
	}
}
