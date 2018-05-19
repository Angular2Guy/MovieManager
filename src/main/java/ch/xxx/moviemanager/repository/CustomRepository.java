package ch.xxx.moviemanager.repository;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.model.Actor;
import ch.xxx.moviemanager.model.Genere;
import ch.xxx.moviemanager.model.Movie;
import ch.xxx.moviemanager.model.User;

@Repository
public class CustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Autowired
	private CrudUserRepository crudUserRep;        
    
    public Optional<Movie> findByMovieId(Integer movieId) {    	
    	try {
        	User user = getCurrentUser();
    		Movie result = em.createQuery("select m from Movie m join m.users u where m.movieid = :movieid and u.id = :userid", Movie.class).setParameter("movieid", movieId).setParameter("userid", user.getId()).getSingleResult();
    		return Optional.ofNullable(result);
    	} catch(RuntimeException e) {
    		return Optional.empty();
    	}
    }
    
    public Optional<Actor> findByActorId(int actorId) {
    	Optional<Actor> result;
    	try {
    		User user = getCurrentUser();
    		Actor res = em.createQuery("select a from Actor a join a.users u where a.actorId = :actorId and u.id = :userid", Actor.class).setParameter("actorId", actorId).setParameter("userid", user.getId()).getSingleResult();
    		result = Optional.of(res);
    	}catch(NoResultException e) {
    		result  = Optional.empty();
    	}
    	return result;
    }
    
    public Optional<Genere> findByGenereId(int genereId) {
    	Optional<Genere> result;
    	try {    		
    		Genere res = em.createQuery("select g from Genere g where g.genereId = :genereId",Genere.class).setParameter("genereId", genereId).getSingleResult();
    		result = Optional.of(res);
    	} catch(NoResultException e) {
    		result = Optional.empty();
    	}
    	return result;
    }
    
    private User getCurrentUser() {
    	return this.crudUserRep
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
}
