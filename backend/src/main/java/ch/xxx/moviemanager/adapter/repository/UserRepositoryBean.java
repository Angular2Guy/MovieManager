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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;
import jakarta.validation.Valid;

@Repository
public class UserRepositoryBean implements UserRepository {
	private final JpaUserRepository jpaUserRepository;
	
	public UserRepositoryBean(JpaUserRepository jpaUserRepository) {
		this.jpaUserRepository = jpaUserRepository;
	}
	@Override
	public Optional<User> findByUsername(String username) {
		return this.jpaUserRepository.findByUsername(username);
	}
	@Override
	public User save(@Valid User user) {
		return this.jpaUserRepository.save(user);
	}

	@Override
	public Optional<User> findById(Long id) {
		return this.jpaUserRepository.findById(id);
	}

	@Override
	public Optional<User> findByUuid(String uuid) {
		return this.jpaUserRepository.findByUuid(uuid);
	}
	
	@Override
	public List<User> findAll() {
		 return StreamSupport.stream(this.jpaUserRepository.findAll().spliterator(), false)
		    .collect(Collectors.toList());
	}
	
	@Override
	public List<User> findOpenMigrations(Long migrationId) {
		return this.jpaUserRepository.findOpenMigrations(migrationId);
	}
	
	@Override
	public Iterable<User> saveAll(Iterable<User> users) {
		return this.jpaUserRepository.saveAll(users);
	}
}
