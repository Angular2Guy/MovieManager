package ch.xxx.moviemanager.adapter.repository;

import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.User;
import ch.xxx.moviemanager.domain.model.UserRepository;

@Repository
public class UserRepositoryBean implements UserRepository {
	private final JpaUserRepository jpaUserRepository;
	
	public UserRepositoryBean(JpaUserRepository jpaUserRepository) {
		this.jpaUserRepository = jpaUserRepository;
	}
	
	public User findByUsername(String username) {
		return this.jpaUserRepository.findByUsername(username);
	}
	
	public User save(User user) {
		return this.jpaUserRepository.save(user);
	}
}
