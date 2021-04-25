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
package ch.xxx.moviemanager.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.model.Actor;
import ch.xxx.moviemanager.model.Movie;
import ch.xxx.moviemanager.model.User;

@Repository
public class CustomRepository {
	private final CrudUserRepository crudUserRep;
	private final CrudActorRepository crudActorRepository;
	private final CrudMovieRepository crudMovieRepository;
	private final EntityManager entityManager;

	public CustomRepository(CrudUserRepository crudUserRep, CrudActorRepository crudActorRepository,
			CrudMovieRepository crudMovieRepository, EntityManager entityManager) {
		this.crudActorRepository = crudActorRepository;
		this.crudMovieRepository = crudMovieRepository;
		this.crudUserRep = crudUserRep;
		this.entityManager = entityManager;
	}

	public Optional<Movie> findByMovieId(Long movieId) {
		User user = getCurrentUser();
		return this.crudMovieRepository.findByMovieId(movieId, user.getId());
	}

	public Optional<Actor> findByActorId(Long actorId) {
			User user = getCurrentUser();
		return this.crudActorRepository.findByActorId(actorId, user.getId());
	}

	public List<Movie> findMoviesByPage(int page) {
		User user = getCurrentUser();
		List<Movie> movies = this.entityManager
				.createQuery("select m from Movie m join m.users u where u.id = :userid order by m.title", Movie.class)
				.setParameter("userid", user.getId()).setFirstResult(10 * (page - 1)).setMaxResults(10).getResultList();
		return movies;
	}

	public List<Actor> findActorsByPage(int page) {
		User user = getCurrentUser();
		List<Actor> actors = this.entityManager
				.createQuery("select a from Actor a join a.users u where u.id = :userid order by a.name", Actor.class)
				.setParameter("userid", user.getId()).setFirstResult(10 * (page - 1)).setMaxResults(10).getResultList();
		return actors;
	}

	private User getCurrentUser() {
		return this.crudUserRep
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
	}
}
