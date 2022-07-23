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
package ch.xxx.moviemanager.adapter.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchStringDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;

@Repository
public class ActorRepositoryBean implements ActorRepository {
	private final JpaActorRepository jpaActorRepository;
	private final EntityManager entityManager;
	
	public ActorRepositoryBean(JpaActorRepository jpaActorRepository, EntityManagerFactory entityManagerFactory) {
		this.jpaActorRepository = jpaActorRepository;
		this.entityManager = entityManagerFactory.createEntityManager();
	}

	public Optional<Actor> findById(Long id) {
		return this.jpaActorRepository.findById(id);
	}
	
	public Actor save(Actor actorEntity) {
		return this.jpaActorRepository.save(actorEntity);
	}
	
	public void deleteById(Long id) {
		this.jpaActorRepository.deleteById(id);
	}
	
	public List<Actor> findByActorName(String name, Long userId, Pageable pageable) {
		return this.jpaActorRepository.findByActorName(name, userId, pageable);
	}
	
	public Optional<Actor> findByActorId(Long actorId, Long userId) {
		return this.jpaActorRepository.findByActorId(actorId, userId);
	}
	
	public List<Actor> findActorsByPage(Long userId, Pageable pageble) {
		return this.findActorsByPage(userId, pageble);
	}

	@Override
	public List<Actor> findUnusedActors() {		
		return this.jpaActorRepository.findUnusedActors();
	}
	
	public List<Actor> findActorsByPhrase(SearchPhraseDto searchPhraseDto) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder actorQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Actor.class).get();
		Query phraseQuery = actorQueryBuilder
				  .phrase()
				  .withSlop(searchPhraseDto.getOtherWordsInPhrase())
				  .onField("biography")
				  .sentence(searchPhraseDto.getPhrase())
				  .createQuery();
		@SuppressWarnings("unchecked")
		List<Actor> resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Actor.class).setMaxResults(50).getResultList();
		return resultList;
	}
	
	public List<Actor> findActorsBySearchStrings(List<SearchStringDto> searchStrings) {
		StringBuilder stringBuilder = new StringBuilder();
		searchStrings.forEach(myDto -> stringBuilder.append(" ").append(myDto.getOperator().value).append(" ").append(myDto.getSearchString()));
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder actorQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Actor.class).get();
		Query phraseQuery = actorQueryBuilder
				  .simpleQueryString()
				  .onField("biography")
				  .matching(stringBuilder.substring(2))
				  .createQuery();
		@SuppressWarnings("unchecked")
		List<Actor> resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Actor.class).setMaxResults(50).getResultList();
		return resultList;
	}
}
