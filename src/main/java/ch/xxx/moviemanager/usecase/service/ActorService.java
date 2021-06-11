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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.xxx.moviemanager.domain.model.dto.ActorDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;

@Service
@Transactional
public class ActorService {
	private static final Logger LOG = LoggerFactory.getLogger(ActorService.class);
	private final DefaultMapper mapper;
	private final ActorRepository actorRep;
	private final UserDetailsMgmtService auds;

	public ActorService(DefaultMapper mapper, ActorRepository actorRep, UserDetailsMgmtService auds) {
		this.mapper = mapper;
		this.actorRep = actorRep;
		this.auds = auds;
	}

	public boolean cleanup() {
		this.actorRep.findUnusedActors().forEach(
				actor -> LOG.info(String.format("Unused Actor id: %d name: %s", actor.getId(), actor.getName())));
		return true;
	}

	public List<Actor> findActor(String name) {
		List<Actor> result = this.actorRep.findByActorName(name, this.auds.getCurrentUser().getId());
		return result;
	}

	public List<Actor> findActorsByPage(Integer page) {
		User currentUser = this.auds.getCurrentUser();
		List<Actor> result = this.actorRep.findActorsByPage(currentUser.getId(), PageRequest.of((page - 1), 10));
		return result;
	}

	public Optional<Actor> findActorById(Long id) {
		Optional<Actor> result = this.actorRep.findById(id);
		if (result.isPresent()) {
			User user = this.auds.getCurrentUser();
			List<Cast> myCasts = result.get().getCasts().stream().filter(c -> c.getMovie().getUsers().contains(user))
					.collect(Collectors.toList());
			result.get().setCasts(myCasts);
			result = result.get().getUsers().stream().filter(myUser -> user.getId().equals(myUser.getId())).findFirst()
					.isEmpty() ? Optional.empty() : result;			
		}
		return result;
	}
}
