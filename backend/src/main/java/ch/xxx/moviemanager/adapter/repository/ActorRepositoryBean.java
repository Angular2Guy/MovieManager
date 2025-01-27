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

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.validation.Valid;

@Repository
public class ActorRepositoryBean implements ActorRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActorRepositoryBean.class);
	private static final String BIOGRAPHY = "biography";
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
	public List<Long> findDublicateActorIds() {
		return this.jpaActorRepository.findDublicateActorIds();
	}
	
	@Override
	public List<Actor> findUnusedActors() {
		return this.jpaActorRepository.findUnusedActors();
	}

	public List<Actor> findByFilterCriteria(ActorFilterCriteriaDto filterCriteriaDto, Long userId) {
		CriteriaQuery<Actor> cq = this.entityManager.getCriteriaBuilder().createQuery(Actor.class);
		Root<Actor> cActor = cq.from(Actor.class);
		List<Predicate> predicates = new ArrayList<>();
		Optional.ofNullable(filterCriteriaDto.getBirthdayFrom())
				.ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(
						cActor.<Date>get("birthday"), CommonUtils.convert(filterCriteriaDto.getBirthdayFrom()))));
		Optional.ofNullable(filterCriteriaDto.getBirthdayTo())
				.ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder().lessThanOrEqualTo(
						cActor.<Date>get("birthday"), CommonUtils.convert(filterCriteriaDto.getBirthdayTo()))));
		Optional.ofNullable(filterCriteriaDto.getDead()).ifPresent(
				x -> predicates.add(this.entityManager.getCriteriaBuilder().isNotNull(cActor.<Date>get("deathday"))));
		Optional.ofNullable(filterCriteriaDto.getGender()).stream().filter(java.util.function.Predicate.not(Gender.Unknown::equals))
				.findFirst().ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder()
						.equal(cActor.get("gender"), filterCriteriaDto.getGender().getCode())));
		Optional.ofNullable(filterCriteriaDto.getName()).stream().filter(myName -> myName.trim().length() > 2)
				.findFirst()
				.ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder().like(
						this.entityManager.getCriteriaBuilder().lower(cActor.get("name")),
						String.format("%%%s%%", filterCriteriaDto.getName().toLowerCase()))));
		if (filterCriteriaDto.getPopularity() > 0) {
			predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(cActor.get("popularity"),
					Double.valueOf(Integer.valueOf(filterCriteriaDto.getPopularity()).toString())));
		}
		Optional.ofNullable(filterCriteriaDto.getMovieCharacter()).stream()
				.filter(myCharacter -> myCharacter.trim().length() > 2).findFirst().ifPresent(x -> {
					Metamodel m = this.entityManager.getMetamodel();
					EntityType<Actor> actor_ = m.entity(Actor.class);
					predicates.add(this.entityManager.getCriteriaBuilder()
							.like(this.entityManager.getCriteriaBuilder()
									.lower(cActor.join(actor_.getDeclaredList("casts", Cast.class)).get("movieChar")),
									String.format("%%%s%%", filterCriteriaDto.getMovieCharacter().toLowerCase())));
				});
		// user check
		Metamodel m = this.entityManager.getMetamodel();
		EntityType<Actor> actor_ = m.entity(Actor.class);
		predicates.add(this.entityManager.getCriteriaBuilder()
				.equal(cActor.join(actor_.getDeclaredSet("users", User.class)).get("id"), userId));
		cq.where(predicates.toArray(new Predicate[0])).distinct(true);
		return this.entityManager.createQuery(cq).setMaxResults(1000).getResultList();
	}

	public List<Actor> findActorsByPhrase(SearchPhraseDto searchPhraseDto) {
		final List<Actor> resultList = new ArrayList<>();
		Optional.ofNullable(searchPhraseDto.getPhrase()).stream().filter(myPhrase -> myPhrase.trim().length() > 2)
				.findFirst().ifPresent(x -> {
					SearchSession searchSession = Search.session(this.entityManager);
					resultList.addAll(searchSession.search(Actor.class).where(f -> f.phrase().field(BIOGRAPHY)
							.matching(searchPhraseDto.getPhrase()).slop(searchPhraseDto.getOtherWordsInPhrase()))
							.fetchHits(1000));
				});
		return resultList;
	}

	public List<Actor> findActorsBySearchStrings(List<SearchStringDto> searchStrings) {
		List<Actor> resultList = Search.session(this.entityManager).search(Actor.class).where((f, root) -> {
			root.add(f.matchAll());
			searchStrings.forEach(myDto -> {
//						LOGGER.info("Op: {}, Val: {}", myDto.getOperator(), myDto.getSearchString());
				switch (myDto.getOperator()) {
				case SearchStringDto.Operator.AND ->
					root.add(f.match().field(BIOGRAPHY).matching(myDto.getSearchString()));
				case SearchStringDto.Operator.NOT ->
					root.add(f.not(f.match().field(BIOGRAPHY).matching(myDto.getSearchString())));
				case SearchStringDto.Operator.OR ->
					root.add(f.or().add(f.match().field(BIOGRAPHY).matching(myDto.getSearchString())));
				}
			});
		}).fetchHits(1000);
		return resultList;
	}

	@Override
	public List<Actor> findByActorIdIn(List<Long> ids) {		
		return this.jpaActorRepository.findByActorIdIn(ids);
	}
}
