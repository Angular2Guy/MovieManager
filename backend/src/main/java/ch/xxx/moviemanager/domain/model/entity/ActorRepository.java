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
package ch.xxx.moviemanager.domain.model.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import ch.xxx.moviemanager.domain.model.dto.ActorFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchStringDto;

public interface ActorRepository {
	List<Actor> findActorsByPage(Long userId, Pageable pageble);
	List<Actor> findByActorName(String name, Long userId, Pageable pageable);
	Optional<Actor> findByActorId(Long actorId, Long userId);
	void deleteById(Long id);
	Optional<Actor> findById(Long id);
	Actor save(Actor actorEntity);
	List<Actor> findUnusedActors();
	List<Actor> findActorsByPhrase(SearchPhraseDto searchPhraseDto);
	List<Actor> findActorsBySearchStrings(String searchString);
	List<Actor> findByFilterCriteria(ActorFilterCriteriaDto filterCriteriaDto, Long userId);
}
