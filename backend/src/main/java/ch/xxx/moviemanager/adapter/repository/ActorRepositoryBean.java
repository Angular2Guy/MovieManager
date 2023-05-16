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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.validation.Valid;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.common.CommonUtils;
import ch.xxx.moviemanager.domain.model.dto.ActorDto.Gender;
import ch.xxx.moviemanager.domain.model.dto.ActorFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchStringDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.User;

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

	public Actor save(@Valid Actor actorEntity) {
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

	public List<Actor> findByFilterCriteria(ActorFilterCriteriaDto filterCriteriaDto, Long userId) {
		CriteriaQuery<Actor> cq = this.entityManager.getCriteriaBuilder().createQuery(Actor.class);
		Root<Actor> cActor = cq.from(Actor.class);
		List<Predicate> predicates = new ArrayList<>();
		if (filterCriteriaDto.getBirthdayFrom() != null) {
			predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(cActor.<Date>get("birthday"),
					CommonUtils.convert(filterCriteriaDto.getBirthdayFrom())));
		}
		if (filterCriteriaDto.getBirthdayTo() != null) {
			predicates.add(this.entityManager.getCriteriaBuilder().lessThanOrEqualTo(cActor.<Date>get("birthday"),
					CommonUtils.convert(filterCriteriaDto.getBirthdayTo())));
		}
		if (filterCriteriaDto.getDead()) {
			predicates.add(this.entityManager.getCriteriaBuilder().isNotNull(cActor.<Date>get("deathday")));
		}
		if (filterCriteriaDto.getGender() != null && !filterCriteriaDto.getGender().equals(Gender.Unknown)) {
			predicates.add(this.entityManager.getCriteriaBuilder().equal(cActor.get("gender"),
					filterCriteriaDto.getGender().getCode()));
		}
		if (filterCriteriaDto.getName() != null && filterCriteriaDto.getName().trim().length() > 2) {
			predicates.add(this.entityManager.getCriteriaBuilder().like(
					this.entityManager.getCriteriaBuilder().lower(cActor.get("name")),
					String.format("%%%s%%", filterCriteriaDto.getName().toLowerCase())));
		}
		if (filterCriteriaDto.getPopularity() > 0) {
			predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(cActor.get("popularity"),
					Double.valueOf(Integer.valueOf(filterCriteriaDto.getPopularity()).toString())));
		}
		if (filterCriteriaDto.getMovieCharacter() != null
				&& filterCriteriaDto.getMovieCharacter().trim().length() > 2) {
			Metamodel m = this.entityManager.getMetamodel();
			EntityType<Actor> actor_ = m.entity(Actor.class);
			predicates
					.add(this.entityManager
							.getCriteriaBuilder().like(
									this.entityManager.getCriteriaBuilder()
											.lower(cActor.join(actor_.getDeclaredList("casts", Cast.class))
													.get("movieChar")),
									String.format("%%%s%%", filterCriteriaDto.getMovieCharacter().toLowerCase())));
		}
		// user check
		Metamodel m = this.entityManager.getMetamodel();
		EntityType<Actor> actor_ = m.entity(Actor.class);
		predicates.add(this.entityManager.getCriteriaBuilder()
				.equal(cActor.join(actor_.getDeclaredSet("users", User.class)).get("id"), userId));
		cq.where(predicates.toArray(new Predicate[0])).distinct(true);
		return this.entityManager.createQuery(cq).setMaxResults(1000).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Actor> findActorsByPhrase(SearchPhraseDto searchPhraseDto) {
		List<Actor> resultList = List.of();
		if (searchPhraseDto.getPhrase() != null && searchPhraseDto.getPhrase().trim().length() > 2) {
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			QueryBuilder actorQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
					.forEntity(Actor.class).get();
			Query phraseQuery = actorQueryBuilder.phrase().withSlop(searchPhraseDto.getOtherWordsInPhrase())
					.onField("biography").sentence(searchPhraseDto.getPhrase()).createQuery();
			resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Actor.class).setMaxResults(1000)
					.getResultList();
		}
		return resultList;
	}

	public List<Actor> findActorsBySearchStrings(List<SearchStringDto> searchStrings) {
		StringBuilder stringBuilder = new StringBuilder();
		searchStrings.forEach(myDto -> stringBuilder.append(" ").append(myDto.getOperator().value).append(" ")
				.append(myDto.getSearchString()));
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder actorQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Actor.class).get();
		Query phraseQuery = actorQueryBuilder.simpleQueryString().onField("biography")
				.matching(stringBuilder.substring(2)).createQuery();
		@SuppressWarnings("unchecked")
		List<Actor> resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Actor.class).setMaxResults(1000)
				.getResultList();
		return resultList;
	}
}
