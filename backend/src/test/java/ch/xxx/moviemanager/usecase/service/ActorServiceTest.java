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
package ch.xxx.moviemanager.usecase.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import ch.xxx.moviemanager.domain.model.dto.ActorFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;

@ExtendWith(MockitoExtension.class)
public class ActorServiceTest {

	@Mock
	private ActorRepository actorRep;

	@Mock
	private UserDetailService userDetailService;

	@Mock
	private DefaultMapper mapper;

	@InjectMocks
	private ActorService actorService;

	@Test
	public void testCleanup() {
		List<Actor> unusedActors = List.of(new Actor());
		Mockito.when(actorRep.findUnusedActors()).thenReturn(unusedActors);
		Assertions.assertTrue(actorService.cleanup());
		Mockito.verify(actorRep).findUnusedActors();
	}

	@Test
	public void testFindActor() {
		Actor myActor = createTestActorEntity();
		User user = new User();
		user.setId(1L);
		Mockito.when(userDetailService.getCurrentUser("YYY")).thenReturn(user);
		Mockito.when(actorRep.findByActorName("XXX", 1L, PageRequest.of(0, 15, Sort.by("name").ascending())))
				.thenReturn(List.of(myActor));
		List<Actor> actors = actorService.findActor("XXX", "YYY");
		Assertions.assertNotNull(actors);
		Assertions.assertEquals(actors.get(0).getId(), myActor.getId());
	}

	@Test
	public void testFindActorsByPage() {
		Actor myActor = createTestActorEntity();
		User user = new User();
		user.setId(1L);
		Mockito.when(userDetailService.getCurrentUser("YYY")).thenReturn(user);
		Mockito.when(actorRep.findActorsByPage(1L, PageRequest.of((0), 10))).thenReturn(List.of(myActor));
		List<Actor> actors = actorService.findActorsByPage(1, "YYY");
		Assertions.assertNotNull(actors);
		Assertions.assertEquals(actors.get(0).getId(), myActor.getId());
	}

	@Test
	public void testFindActorById() {
		Actor myActor = createTestActorEntity();
		User user = new User();
		user.setId(1L);
		var cast = new Cast();
		var movie = new Movie();
		movie.getUsers().add(user);
		cast.setMovie(movie);
		myActor.getCasts().add(cast);
		Mockito.when(userDetailService.getCurrentUser("YYY")).thenReturn(user);
		Mockito.when(actorRep.findById(any())).thenReturn(Optional.of(myActor));
		Optional<Actor> actorOpt = actorService.findActorById(1L, "YYY");
		Assertions.assertTrue(actorOpt.isPresent());
		Assertions.assertEquals(actorOpt.get().getId(), myActor.getId());
	}

	@Test
	public void testFindActorsByFilterCriteria() {
		ActorFilterCriteriaDto filterCriteriaDto = new ActorFilterCriteriaDto();
		filterCriteriaDto.setSearchTermDto(this.createSearchTermDto());
		Mockito.when(userDetailService.getCurrentUser("YYY"))
				.thenReturn(this.createTestActorEntity().getUsers().stream().findFirst().orElseThrow());
		List<Actor> jpaActors = List.of(createTestActorEntity());
		List<Actor> ftActors = List.of(createTestActorEntity());
		Mockito.when(actorRep.findByFilterCriteria(any(), any())).thenReturn(jpaActors);
		Mockito.when(actorRep.findActorsByPhrase(any())).thenReturn(ftActors);
		List<Actor> actors = actorService.findActorsByFilterCriteria("YYY", filterCriteriaDto);
		Assertions.assertNotNull(actors);
	}

	@Test
	public void testFindActorsBySearchTerm() {
		SearchTermDto searchTermDto = createSearchTermDto();
		Mockito.when(userDetailService.getCurrentUser("YYY"))
				.thenReturn(this.createTestActorEntity().getUsers().stream().findFirst().orElseThrow());
		List<Actor> actors = List.of(createTestActorEntity());
		Mockito.when(actorRep.findActorsByPhrase(any())).thenReturn(actors);
		List<Actor> foundActors = actorService.findActorsBySearchTerm("YYY", searchTermDto);
		Assertions.assertNotNull(foundActors);
	}

	private SearchTermDto createSearchTermDto() {
		SearchTermDto searchTermDto = new SearchTermDto();
		var searchPhraseDto = new SearchPhraseDto();
		searchPhraseDto.setPhrase("hallo");
		searchTermDto.setSearchPhraseDto(searchPhraseDto);
		return searchTermDto;
	}

	private Actor createTestActorEntity() {
		Actor myActor = new Actor();
		myActor.setId(1L);
		myActor.setName("myName");
		var user = new User();
		user.setId(1L);
		myActor.getUsers().add(user);
		return myActor;
	}
}