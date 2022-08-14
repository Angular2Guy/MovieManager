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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.dto.FilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchStringDto;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;

@Repository
public class MovieRepositoryBean implements MovieRepository {
	private final JpaMovieRepository jpaMovieRepository;
	private final EntityManager entityManager;

	public MovieRepositoryBean(JpaMovieRepository jpaMovieRepository, EntityManagerFactory entityManagerFactory) {
		this.jpaMovieRepository = jpaMovieRepository;
		this.entityManager = entityManagerFactory.createEntityManager();

	}

	@Override
	public Optional<Movie> findById(Long id) {
		return this.jpaMovieRepository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		this.jpaMovieRepository.deleteById(id);
	}

	@Override
	public Movie save(Movie movieEntity) {
		return this.jpaMovieRepository.save(movieEntity);
	}

	@Override
	public List<Movie> findByTitle(String title, Long userId, Pageable pageable) {
		return this.jpaMovieRepository.findByTitle(title, userId, pageable);
	}

	@Override
	public List<Movie> findByGenereId(Long id, Long userId) {
		return this.jpaMovieRepository.findByGenereId(id, userId);
	}

	@Override
	public List<Movie> findByTitleAndRelDate(String title, Date releaseDate, Long userId) {
		return this.jpaMovieRepository.findByTitleAndRelDate(title, releaseDate, userId);
	}

	@Override
	public Optional<Movie> findByMovieId(Long movieId, Long userId) {
		return this.jpaMovieRepository.findByMovieId(movieId, userId);
	}

	@Override
	public List<Movie> findMoviesByPage(Long userId, Pageable pageable) {
		return this.jpaMovieRepository.findMoviesByPage(userId, pageable);
	}

	@Override
	public List<Movie> findUnusedMovies() {
		return this.jpaMovieRepository.findUnusedMovies();
	}

	@Override
	public Optional<Movie> findByIdWithCollections(Long ids) {
		return this.jpaMovieRepository.findByIdWithCollections(ids);
	}

	public List<Movie> findByFilterCriteria(FilterCriteriaDto filterCriteriaDto) {
		CriteriaQuery<Movie> cq = this.entityManager.getCriteriaBuilder().createQuery(Movie.class);
		Root<Movie> cMovie = cq.from(Movie.class);
		List<Predicate> predicates = new ArrayList<>();
		if (filterCriteriaDto.getReleaseFrom() != null) {
			predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(cMovie.<Date>get("releaseDate"),
					this.convert(filterCriteriaDto.getReleaseFrom())));
		}
		if (filterCriteriaDto.getReleaseTo() != null) {
			predicates.add(this.entityManager.getCriteriaBuilder().lessThanOrEqualTo(cMovie.<Date>get("releaseDate"),
					this.convert(filterCriteriaDto.getReleaseTo())));
		}
		if (filterCriteriaDto.getMovieTitle() != null && filterCriteriaDto.getMovieTitle().trim().length() > 2) {
			predicates.add(this.entityManager.getCriteriaBuilder().like(
					this.entityManager.getCriteriaBuilder().lower(cMovie.get("title")),
					String.format("%%%s%%", filterCriteriaDto.getMovieTitle().toLowerCase())));
		}
		if (filterCriteriaDto.getMovieActor() != null && filterCriteriaDto.getMovieActor().trim().length() > 2) {
			Metamodel m = this.entityManager.getMetamodel();
			EntityType<Movie> movie_ = m.entity(Movie.class);
			predicates
					.add(this.entityManager
							.getCriteriaBuilder().like(
									this.entityManager.getCriteriaBuilder()
											.lower(cMovie.join(movie_.getDeclaredCollection("cast", Cast.class))
													.get("movieChar")),
									String.format("%%%s%%", filterCriteriaDto.getMovieActor().toLowerCase())));
		}
		if (filterCriteriaDto.getMinLength() > 0) {
			predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(cMovie.get("runtime"),
					filterCriteriaDto.getMinLength()));
		}
		if (filterCriteriaDto.getMaxLength() > 0) {
			predicates.add(this.entityManager.getCriteriaBuilder().lessThanOrEqualTo(cMovie.get("runtime"),
					filterCriteriaDto.getMaxLength()));
		}
		if (filterCriteriaDto.getMinRating() > 0) {
			predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(cMovie.get("voteAverage"),
					filterCriteriaDto.getMinRating()));
		}
		cq.where(predicates.toArray(new Predicate[0]));
		return this.entityManager.createQuery(cq).getResultList();
	}

	private Date convert(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@SuppressWarnings("unchecked")
	public List<Movie> findMoviesByPhrase(SearchPhraseDto searchPhraseDto) {
		List<Movie> resultList = List.of();
		if (searchPhraseDto.getPhrase() != null && searchPhraseDto.getPhrase().trim().length() > 2) {
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			QueryBuilder movieQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
					.forEntity(Movie.class).get();
			Query phraseQuery = movieQueryBuilder.phrase().withSlop(searchPhraseDto.getOtherWordsInPhrase())
					.onField("overview").sentence(searchPhraseDto.getPhrase()).createQuery();
			resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Movie.class).setMaxResults(50)
					.getResultList();
		}
		return resultList;
	}

	public List<Movie> findMoviesBySearchStrings(List<SearchStringDto> searchStrings) {
		StringBuilder stringBuilder = new StringBuilder();
		searchStrings.stream().filter(
				searchStringDto -> searchStringDto.getOperator() != null && searchStringDto.getSearchString() != null)
				.toList().forEach(myDto -> stringBuilder.append(" ").append(myDto.getOperator().value).append(" ")
						.append(myDto.getSearchString()));
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder actorQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Movie.class).get();
		Query phraseQuery = actorQueryBuilder.simpleQueryString().onField("biography")
				.matching(stringBuilder.substring(2)).createQuery();
		@SuppressWarnings("unchecked")
		List<Movie> resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Movie.class).setMaxResults(50)
				.getResultList();
		return resultList;
	}
}
