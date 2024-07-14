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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.xxx.moviemanager.domain.model.dto.ActorFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.User;

@ExtendWith(SpringExtension.class)
public class ActorServiceTest {
	@Mock
	private ActorRepository actorRep;
	@Mock
	private UserDetailService userDetailService;
	@InjectMocks
	private ActorService movieService;

	@Test
	public void findActorsByPage() throws Exception {
		final Actor myActor = new Actor();
		myActor.setId(1L);
		myActor.setName("myName");
		User user = new User();
		user.setId(1L);
		myActor.getUsers().add(user);
		Mockito.when(this.actorRep.findActorsByPage(any(Long.class), any(PageRequest.class)))
				.thenReturn(List.of(myActor));
		final User myUser = new User();
		myUser.setId(1L);
		Mockito.when(this.userDetailService.getCurrentUser(any(String.class))).thenReturn(myUser);
		List<Actor> actors = this.movieService.findActorsByPage(1, "");
		Assertions.assertNotNull(actors);
		Assertions.assertEquals(actors.get(0).getId(), myActor.getId());
	}

	@Test
	public void findActorById() throws Exception {
		final Actor myActor = new Actor();
		myActor.setId(1L);
		myActor.setName("myName");
		User user = new User();
		user.setId(1L);
		myActor.getUsers().add(user);
		Mockito.when(this.actorRep.findById(any(Long.class))).thenReturn(Optional.of(myActor));
		final User myUser = new User();
		myUser.setId(1L);
		Mockito.when(this.userDetailService.getCurrentUser(any(String.class))).thenReturn(myUser);
		Optional<Actor> actorOpt = this.movieService.findActorById(1L, "");
		Assertions.assertTrue(actorOpt.isPresent());
		Assertions.assertEquals(actorOpt.get().getId(), myActor.getId());
	}

	@Test
	public void findActorsByFilterCriteria() throws Exception {
		final Actor myActor = new Actor();
		myActor.setId(1L);
		myActor.setName("myName");
		User user = new User();
		user.setId(1L);
		myActor.getUsers().add(user);
		Mockito.when(this.actorRep.findByFilterCriteria(any(), any(Long.class))).thenReturn(List.of(myActor));
		final User myUser = new User();
		myUser.setId(1L);
		Mockito.when(this.userDetailService.getCurrentUser(any(String.class))).thenReturn(myUser);
		ActorFilterCriteriaDto filterCriteria = new ActorFilterCriteriaDto();
		SearchTermDto searchTerm = new SearchTermDto();
		var searchPhraseDto = new SearchPhraseDto();
		searchPhraseDto.setPhrase("mySearch");
		searchPhraseDto.setOtherWordsInPhrase(0);
		searchTerm.setSearchPhraseDto(searchPhraseDto);
		filterCriteria.setSearchTermDto(searchTerm);
		List<Actor> actors = this.movieService.findActorsByFilterCriteria("", filterCriteria);
		Assertions.assertNotNull(actors);
		assertThat(actors.get(0)).extracting(Actor::getName).isEqualTo("myName");
	}

	@Test
	public void cleanup() throws Exception {
		Mockito.when(this.actorRep.findUnusedActors()).thenReturn(List.of());
		boolean result = this.movieService.cleanup();
		Assertions.assertTrue(result);
	}
}