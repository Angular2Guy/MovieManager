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

import java.util.Date;
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

	public List<Movie> findMoviesByPhrase(SearchPhraseDto searchPhraseDto) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder movieQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Movie.class).get();
		Query phraseQuery = movieQueryBuilder.phrase().withSlop(searchPhraseDto.getOtherWordsInPhrase())
				.onField("overview").sentence(searchPhraseDto.getPhrase()).createQuery();
		@SuppressWarnings("unchecked")
		List<Movie> resultList = fullTextEntityManager.createFullTextQuery(phraseQuery, Movie.class).setMaxResults(50)
				.getResultList();
		return resultList;
	}

	public List<Movie> findMoviesBySearchStrings(List<SearchStringDto> searchStrings) {
		StringBuilder stringBuilder = new StringBuilder();
		searchStrings.forEach(myDto -> stringBuilder.append(" ").append(myDto.getOperator().value).append(" ")
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
