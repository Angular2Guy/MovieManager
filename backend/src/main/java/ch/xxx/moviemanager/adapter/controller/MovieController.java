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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.domain.exceptions.ResourceNotFoundException;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieDto;
import ch.xxx.moviemanager.domain.model.dto.MovieFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.service.MovieService;

@RestController
@RequestMapping("rest/movie")
public class MovieController {
	private static final Logger LOG = LoggerFactory.getLogger(MovieController.class);
	private final MovieService service;
	private final DefaultMapper mapper;

	public MovieController(MovieService service, DefaultMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@RequestMapping(value = "/{title}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MovieDto> getMovieSearch(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr,
			@PathVariable("title") String titleStr) throws InterruptedException {
		List<MovieDto> movies = this.service.findMovie(titleStr, bearerStr).stream()
				.map(m -> this.mapper.convertOnlyMovie(m)).toList();
		return movies;
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public MovieDto getMovieSearchById(@PathVariable("id") Long id,@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr) throws InterruptedException {
		MovieDto result = this.mapper.convert(this.service.findMovieById(id, bearerStr).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Failed to find movie with id: %d", id))));
		return result;
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMovieById(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr,
			@PathVariable("id") Long id) throws InterruptedException {
		boolean result = this.service.deleteMovieById(id, bearerStr);
		return result ? new ResponseEntity<Boolean>(result, HttpStatus.OK)
				: new ResponseEntity<Boolean>(result, HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/genere/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MovieDto> getGeneresById(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr,
			@PathVariable("id") Long id) throws InterruptedException {
		List<MovieDto> movies = this.service.findMoviesByGenereId(id, bearerStr).stream()
				.map(this.mapper::convertMovieWithGenere).toList();
		return movies;
	}

	@RequestMapping(value = "/generes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<GenereDto> getGeneres() throws InterruptedException {
		List<GenereDto> generes = this.service.findAllGeneres().stream().map(this.mapper::convert).toList();
		return generes;
	}

	@RequestMapping(value = "/pages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MovieDto> getPagesByNumber(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr,
			Pageable pageable) throws InterruptedException {
		LOG.debug(String.format("page=%d, size=%d, sort=%s", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
		List<MovieDto> movies = this.service.findMoviesByPage(pageable.getPageNumber(), bearerStr).stream().map(this.mapper::convert)
				.toList();
		return movies;
	}

	@RequestMapping(value = "/filter-criteria", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<MovieDto> getMoviesByCriteria(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr,
			@RequestBody MovieFilterCriteriaDto filterCriteria) {
		return this.service.findMoviesByFilterCriteria(bearerStr, filterCriteria).stream()
				.map(this.mapper::convert).toList();
	}

	@RequestMapping(value = "/searchterm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<MovieDto> postSearchTerm(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr,
			SearchTermDto searchTermDto) {
		List<MovieDto> results = this.service.findMoviesBySearchTerm(bearerStr, searchTermDto).stream()
				.map(this.mapper::convert).toList();
		return results;
	}
}
