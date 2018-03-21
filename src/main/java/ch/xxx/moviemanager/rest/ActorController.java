package ch.xxx.moviemanager.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.dto.ActorDto;
import ch.xxx.moviemanager.service.MovieManagerService;

@RestController
@RequestMapping("rest/actor")
public class ActorController {
	@Autowired
	private MovieManagerService service;
	
	@RequestMapping(value="/{name}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<ActorDto>> getActorSearch(@PathVariable("name") String name) throws InterruptedException {
		List<ActorDto> actors = this.service.findActor(name);		
		return new ResponseEntity<List<ActorDto>>(actors, HttpStatus.OK);		
	}
	
	@RequestMapping(value="/id/{id}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ActorDto> getActorSearchById(@PathVariable("id") Long id) throws InterruptedException {
		Optional<ActorDto> actor = this.service.findActorById(id);
		if(actor.isPresent()) {
			return new ResponseEntity<ActorDto>(actor.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<ActorDto>(HttpStatus.NOT_FOUND);
		}
	}
}
