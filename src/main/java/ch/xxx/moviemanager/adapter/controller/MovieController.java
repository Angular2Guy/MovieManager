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
package ch.xxx.moviemanager.adapter.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.domain.exceptions.ResourceNotFoundException;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.service.MovieService;

@RestController
@RequestMapping("rest/movie")
public class MovieController {
	private final MovieService service;
	private final DefaultMapper mapper;

	public MovieController(MovieService service, DefaultMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@RequestMapping(value = "/{title}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getMovieSearch(@PathVariable("title") String titleStr)
			throws InterruptedException {
		List<MovieDto> movies = this.service.findMovie(titleStr).stream().map(m -> this.mapper.convert(m))
				.collect(Collectors.toList());
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDto> getMovieSearchById(@PathVariable("id") Long id) throws InterruptedException {
		MovieDto result = this.mapper.convert(this.service.findMovieById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Failed to find movie with id: %d", id))));
		return new ResponseEntity<MovieDto>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMovieById(@PathVariable("id") Long id) throws InterruptedException {
		boolean result = this.service.deleteMovieById(id);
		return result ? new ResponseEntity<Boolean>(result, HttpStatus.OK)
				: new ResponseEntity<Boolean>(result, HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/genere/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getGeneresById(@PathVariable("id") Long id) throws InterruptedException {
		List<MovieDto> movies = this.service.findMoviesByGenere(id).stream().map(m -> this.mapper.convertMovieWithGenere(m))
				.collect(Collectors.toList());
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);
	}

	@RequestMapping(value = "/generes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<GenereDto>> getGeneres() throws InterruptedException {
		List<GenereDto> generes = this.service.findAllGeneres().stream().map(gen -> this.mapper.convert(gen))
				.collect(Collectors.toList());
		return new ResponseEntity<List<GenereDto>>(generes, HttpStatus.OK);
	}

	@RequestMapping(value = "/pages", params = {
			"page" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getPagesByNumber(@RequestParam("page") Integer page)
			throws InterruptedException {
		List<MovieDto> movies = this.service.findMoviesByPage(page).stream().map(m -> this.mapper.convert(m))
				.collect(Collectors.toList());
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);
	}

}
