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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.domain.exceptions.ResourceNotFoundException;
import ch.xxx.moviemanager.usecase.model.MovieDto;
import ch.xxx.moviemanager.usecase.service.MovieManagerService;

@RestController
@RequestMapping("rest/movie")
public class MovieController {
	@Autowired
	private MovieManagerService service;
	
	@RequestMapping(value="/{title}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getMovieSearch(@PathVariable("title") String titleStr) throws InterruptedException {
		List<MovieDto> movies = this.service.findMovie(titleStr);		
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);		
	}
	
	@RequestMapping(value="/id/{id}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDto> getMovieSearchById(@PathVariable("id") Long id) throws InterruptedException {
		Optional<MovieDto> result = this.service.findMovieById(id);
		if(result.isPresent()) {
			return new ResponseEntity<MovieDto>(result.get(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException(String.format("Failed to find movie with id: %s", id.toString()));
		}
	}
	
	@RequestMapping(value="/id/{id}", method=RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMovieById(@PathVariable("id") Long id) throws InterruptedException {
		boolean result = this.service.deleteMovieById(id);
		return result ? new ResponseEntity<Boolean>(result, HttpStatus.OK) : new ResponseEntity<Boolean>(result, HttpStatus.NOT_FOUND);		
	}
	
	@RequestMapping(value="/genere/id/{id}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getGeneresById(@PathVariable("id") Long id) throws InterruptedException {
		List<MovieDto> movies = this.service.findMoviesByGenere(id);	
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);		
	}
	
	@RequestMapping(value="/pages", params = {"page"}, method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getPagesByNumber(@RequestParam("page") Integer page) throws InterruptedException {
		List<MovieDto> movies = this.service.findMoviesByPage(page);	
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);		
	}
	
}
