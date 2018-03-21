package ch.xxx.moviemanager.repository;

import org.springframework.data.repository.CrudRepository;

import ch.xxx.moviemanager.model.User;

public interface CrudUserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
