package ch.xxx.moviemanager.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.dto.MovieDto;
import ch.xxx.moviemanager.service.MovieManagerService;

@RestController
@RequestMapping("rest/movie/import")
public class MovieImportContoller {
	@Autowired
	private MovieManagerService service;
	
	@RequestMapping(value="/{searchStr}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieDto>> getMovieImportSearch(@PathVariable("searchStr") String searchStr) throws InterruptedException {
		List<MovieDto> movies = this.service.findImportMovie(searchStr);		
		return new ResponseEntity<List<MovieDto>>(movies, HttpStatus.OK);		
	}
	
	@RequestMapping(value="/{searchStr}/number/{number}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> getMovieImport(@PathVariable("searchStr") String searchStr,@PathVariable("number") int number) throws InterruptedException {
		boolean success = this.service.importMovie(searchStr, number);
		if(success) {
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		} else {
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NOT_ACCEPTABLE);			
		}
	}
}
