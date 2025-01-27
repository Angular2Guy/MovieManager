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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.xxx.moviemanager.domain.common.CommonUtils;
import ch.xxx.moviemanager.domain.model.dto.ActorFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.User;

@Service
@Transactional
public class ActorService {
	private static final Logger LOG = LoggerFactory.getLogger(ActorService.class);
	private final ActorRepository actorRep;
	private final UserDetailService auds;

	public ActorService(ActorRepository actorRep, UserDetailService auds) {
		this.actorRep = actorRep;
		this.auds = auds;
	}

	public boolean cleanup() {
		this.actorRep.findUnusedActors().forEach(
				actor -> LOG.info(String.format("Unused Actor id: %d name: %s", actor.getId(), actor.getName())));
		//fix for actor dublicates
		/*
		final var actorMap = this.actorRep.findByActorIdIn(this.actorRep.findDublicateActorIds()).stream()
				.collect(Collectors.groupingBy(actor -> actor.getActorId(), Collectors.toList()));
		final var resultCount = new AtomicLong(0L);
		actorMap.forEach((actorId, myActors) -> {
			if (myActors.size() > 1) {
				myActors.stream().filter(actor -> !actor.equals(myActors.getFirst())).forEach(actor -> {
					this.actorRep.deleteById(actor.getId());
					resultCount.set(resultCount.get() + 1);
				});
			}
		});
		LOG.info("Actors deleted {}.", resultCount.get());
		*/
		return true;
	}

	public List<Actor> findActor(String name, String bearerStr) {
		PageRequest pageRequest = PageRequest.of(0, 15, Sort.by("name").ascending());
		List<Actor> result = this.actorRep.findByActorName(name, this.auds.getCurrentUser(bearerStr).getId(),
				pageRequest);
		return result;
	}

	public List<Actor> findActorsByPage(Integer page, String bearerStr) {
		User currentUser = this.auds.getCurrentUser(bearerStr);
		List<Actor> result = this.actorRep.findActorsByPage(currentUser.getId(), PageRequest.of((page - 1), 10));
		return result;
	}

	public Optional<Actor> findActorById(Long id, String bearerStr) {
		final User user = this.auds.getCurrentUser(bearerStr);
		Optional<Actor> result = this.actorRep.findById(id)
				.filter(myActor -> myActor.getUsers().stream().anyMatch(myUser -> user.getId().equals(myUser.getId())))
				.filter(myActor -> myActor.getCasts().stream().filter(c -> c.getMovie().getUsers().contains(user))
						.findFirst().isPresent());
		return result;
	}

	public List<Actor> findActorsByFilterCriteria(String bearerStr, ActorFilterCriteriaDto filterCriteriaDto) {
		User currentUser = this.auds.getCurrentUser(bearerStr);
		List<Actor> jpaActors = this.actorRep.findByFilterCriteria(filterCriteriaDto, currentUser.getId());
		List<Actor> ftActors = this.findActorsBySearchTerm(bearerStr, filterCriteriaDto.getSearchTermDto());
		List<Actor> results = jpaActors;
		if (!ftActors.isEmpty()) {
			Collection<Long> dublicates = CommonUtils
					.findDublicates(Stream.of(jpaActors, ftActors).flatMap(List::stream).toList());
			results = Stream.of(jpaActors, ftActors).flatMap(List::stream)
					.filter(myMovie -> CommonUtils.filterForDublicates(myMovie, dublicates)).toList();
			// remove dublicates
			results = results.isEmpty() ? ftActors : List.copyOf(CommonUtils.filterDublicates(results));
		}
		return results.subList(0, results.size() > 50 ? 50 : results.size());
	}

	public List<Actor> findActorsBySearchTerm(String bearerStr, SearchTermDto searchTermDto) {
		List<Actor> filteredActors = List.of();
		if (Optional.ofNullable(searchTermDto.getSearchPhraseDto().getPhrase()).stream().anyMatch(
				myPhrase -> Optional.ofNullable(myPhrase).stream().anyMatch(phrase -> phrase.trim().length() > 2))
				|| !Arrays.asList(searchTermDto.getSearchStringDtos()).isEmpty()) {
			List<Actor> actors = searchTermDto.getSearchPhraseDto() != null
					? this.actorRep.findActorsByPhrase(searchTermDto.getSearchPhraseDto())
					: this.actorRep.findActorsBySearchStrings(Arrays.asList(searchTermDto.getSearchStringDtos()));
			filteredActors = actors.stream()
					.filter(myActor -> myActor.getUsers().stream()
							.anyMatch(myUser -> myUser.getId().equals(this.auds.getCurrentUser(bearerStr).getId())))
					.toList();
		}
		return filteredActors;
	}
}
