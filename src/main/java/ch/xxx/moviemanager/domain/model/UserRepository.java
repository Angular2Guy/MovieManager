package ch.xxx.moviemanager.domain.model;

public interface UserRepository {
	User findByUsername(String username);

	User save(User user);
}
