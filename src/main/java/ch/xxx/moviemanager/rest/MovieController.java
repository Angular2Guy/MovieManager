package ch.xxx.moviemanager.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.dto.GenereDto;
import ch.xxx.moviemanager.dto.MovieDto;
import ch.xxx.moviemanager.service.MovieManagerService;

@RestController
@RequestMapping("rest/movie")
public class MovieController {
	@Autowired
	private MovieManagerService service;
	
	@RequestMapping(value="/{title}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<MovieDto>> getMovieSearch(@PathVariable("title") String titleStr) throws InterruptedException {
		List<MovieDto> movies = this.service.findMovie(titleStr);		
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);		
	}
	
	@RequestMapping(value="/id/{id}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<MovieDto> getMovieSearchById(@PathVariable("id") Long id) throws InterruptedException {
		Optional<MovieDto> result = this.service.findMovieById(id);
		if(result.isPresent()) {
			return new ResponseEntity<MovieDto>(result.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<MovieDto>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value="/id/{id}", method=RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> deleteMovieById(@PathVariable("id") Long id) throws InterruptedException {
		boolean result = this.service.deleteMovieById(id);
		return result ? new ResponseEntity<Boolean>(result, HttpStatus.OK) : new ResponseEntity<Boolean>(result, HttpStatus.NOT_FOUND);		
	}
	
	@RequestMapping(value="/genere/id/{id}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<MovieDto>> getGeneresById(@PathVariable("id") Long id) throws InterruptedException {
		List<MovieDto> movies = this.service.findMoviesByGenere(id);	
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);		
	}
}
