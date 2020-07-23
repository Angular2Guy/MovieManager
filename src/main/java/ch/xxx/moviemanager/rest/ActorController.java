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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.dto.ActorDto;
import ch.xxx.moviemanager.exceptions.ResourceNotFoundException;
import ch.xxx.moviemanager.service.MovieManagerService;

@RestController
@RequestMapping("rest/actor")
public class ActorController {
	@Autowired
	private MovieManagerService service;

	@RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActorDto>> getActorSearch(@PathVariable("name") String name)
			throws InterruptedException {
		List<ActorDto> actors = this.service.findActor(name);
		return new ResponseEntity<List<ActorDto>>(actors, HttpStatus.OK);
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActorDto> getActorSearchById(@PathVariable("id") Long id) throws InterruptedException {
		Optional<ActorDto> actor = this.service.findActorById(id);
		if (actor.isPresent()) {
			return new ResponseEntity<ActorDto>(actor.get(), HttpStatus.OK);
		}
		throw new ResourceNotFoundException(String.format("Failed to find actor with id: %s", id.toString()));
	}

	@RequestMapping(value = "/pages", params = {
			"page" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActorDto>> getPagesByNumber(@RequestParam("page") Integer page)
			throws InterruptedException {
		List<ActorDto> actors = this.service.findActorsByPage(page);
		return new ResponseEntity<List<ActorDto>>(actors, HttpStatus.OK);
	}
}
