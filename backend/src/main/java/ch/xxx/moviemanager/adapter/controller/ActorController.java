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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.domain.exceptions.ResourceNotFoundException;
import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.service.ActorService;
import ch.xxx.moviemanager.usecase.service.UserDetailMgmtService;

@RestController
@RequestMapping("rest/actor")
public class ActorController {
	private final ActorService service;
	private final DefaultMapper mapper;
	private final UserDetailMgmtService auds;

	public ActorController(ActorService service, DefaultMapper mapper, UserDetailMgmtService auds) {
		this.service = service;
		this.mapper = mapper;
		this.auds = auds;
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActorDto>> getActorSearch(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr, @PathVariable("name") String name)
			throws InterruptedException {
		List<ActorDto> actors = this.service.findActor(name, bearerStr).stream()
				.map(a -> this.mapper.convertOnlyActor(a)).collect(Collectors.toList());
		return new ResponseEntity<List<ActorDto>>(actors, HttpStatus.OK);
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActorDto> getActorSearchById(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr, @PathVariable("id") Long id)
			throws InterruptedException {
		final User currentUser = this.auds.getCurrentUser(bearerStr);
		ActorDto actor = this.service.findActorById(id, bearerStr).stream()
				.filter(myActor -> myActor.getCasts().stream()
						.filter(c -> c.getMovie().getUsers().contains(currentUser)).findFirst().isPresent())
				.map(a -> this.mapper.convert(a)).findFirst().orElseThrow(
						() -> new ResourceNotFoundException(String.format("Failed to find actor with id: %d", id)));
		return new ResponseEntity<ActorDto>(actor, HttpStatus.OK);
	}

	@RequestMapping(value = "/pages", params = {
			"page" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActorDto>> getPagesByNumber(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr, @RequestParam("page") Integer page)
			throws InterruptedException {
		List<ActorDto> actors = this.service.findActorsByPage(page, bearerStr).stream().map(a -> this.mapper.convert(a))
				.collect(Collectors.toList());
		return new ResponseEntity<List<ActorDto>>(actors, HttpStatus.OK);
	}

	@RequestMapping(value = "/searchterm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActorDto>> postSearchTerm(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerStr, SearchTermDto searchTermDto) {
		return null;
	}
}
