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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.common.CommonUtils;
import ch.xxx.moviemanager.domain.model.dto.GenereDto;
import ch.xxx.moviemanager.domain.model.dto.MovieFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchPhraseDto;
import ch.xxx.moviemanager.domain.model.dto.SearchStringDto;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.Genere;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;
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
	public Movie save(@Valid Movie movieEntity) {
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

	public List<Movie> findByFilterCriteria(MovieFilterCriteriaDto filterCriteriaDto, Long userId) {
		CriteriaQuery<Movie> cq = this.entityManager.getCriteriaBuilder().createQuery(Movie.class);
		Root<Movie> cMovie = cq.from(Movie.class);
		List<Predicate> predicates = new ArrayList<>();
		Optional.ofNullable(filterCriteriaDto.getReleaseFrom())
				.ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder().greaterThanOrEqualTo(
						cMovie.<Date>get("releaseDate"), CommonUtils.convert(filterCriteriaDto.getReleaseFrom()))));
		Optional.ofNullable(filterCriteriaDto.getReleaseTo())
				.ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder().lessThanOrEqualTo(
						cMovie.<Date>get("releaseDate"), CommonUtils.convert(filterCriteriaDto.getReleaseTo()))));
		Optional.ofNullable(filterCriteriaDto.getMovieTitle()).stream().filter(myTitle -> myTitle.trim().length() > 2)
				.findFirst()
				.ifPresent(x -> predicates.add(this.entityManager.getCriteriaBuilder().like(
						this.entityManager.getCriteriaBuilder().lower(cMovie.get("title")),
						String.format("%%%s%%", filterCriteriaDto.getMovieTitle().toLowerCase()))));
		Optional.ofNullable(filterCriteriaDto.getMovieActor()).stream().filter(myActor -> myActor.trim().length() > 2)
				.findFirst().ifPresent(x -> {
					Metamodel m = this.entityManager.getMetamodel();
					EntityType<Movie> movie_ = m.entity(Movie.class);
					predicates
							.add(this.entityManager.getCriteriaBuilder().like(
									this.entityManager.getCriteriaBuilder()
											.lower(cMovie.join(movie_.getDeclaredList("cast", Cast.class))
													.get("characterName")),
									String.format("%%%s%%", filterCriteriaDto.getMovieActor().toLowerCase())));
				});
		if (!filterCriteriaDto.getSelectedGeneres().isEmpty()) {
			Metamodel m = this.entityManager.getMetamodel();
			EntityType<Movie> movie_ = m.entity(Movie.class);
			predicates.add(cMovie.join(movie_.getDeclaredSet("generes", Genere.class)).get("genereId")
					.in(filterCriteriaDto.getSelectedGeneres().stream().map(GenereDto::getId).toList()));
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
		// user check
		Metamodel m = this.entityManager.getMetamodel();
		EntityType<Movie> movie_ = m.entity(Movie.class);
		predicates.add(this.entityManager.getCriteriaBuilder()
				.equal(cMovie.join(movie_.getDeclaredSet("users", User.class)).get("id"), userId));
		cq.where(predicates.toArray(new Predicate[0])).distinct(true);
		return this.entityManager.createQuery(cq).setMaxResults(1000).getResultList();
	}

	public List<Movie> findMoviesByPhrase(SearchPhraseDto searchPhraseDto) {
		List<Movie> resultList = List.of();
		if (searchPhraseDto.getPhrase() != null && searchPhraseDto.getPhrase().trim().length() > 2) {
			resultList = Search
					.session(this.entityManager).search(Movie.class).where(f -> f.phrase().field("overview")
							.matching(searchPhraseDto.getPhrase()).slop(searchPhraseDto.getOtherWordsInPhrase()))
					.fetchHits(1000);
		}
		return resultList;
	}

	public List<Movie> findMoviesBySearchStrings(List<SearchStringDto> searchStrings) {
		StringBuilder stringBuilder = new StringBuilder();
		searchStrings.stream().filter(
				searchStringDto -> searchStringDto.getOperator() != null && searchStringDto.getSearchString() != null)
				.toList().forEach(myDto -> stringBuilder.append(" ").append(myDto.getOperator().value).append(" ")
						.append(myDto.getSearchString()));
		List<Movie> resultList = Search.session(this.entityManager).search(Movie.class)
				.where(f -> f.simpleQueryString().field("overview").matching(stringBuilder.substring(2)))
				.fetchHits(1000);
		return resultList;
	}
}
