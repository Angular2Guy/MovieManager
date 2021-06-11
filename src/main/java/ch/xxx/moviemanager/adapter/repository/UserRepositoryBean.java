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

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;

@Repository
public class UserRepositoryBean implements UserRepository {
	private final JpaUserRepository jpaUserRepository;
	
	public UserRepositoryBean(JpaUserRepository jpaUserRepository) {
		this.jpaUserRepository = jpaUserRepository;
	}
	
	public Optional<User> findByUsername(String username) {
		return this.jpaUserRepository.findByUsername(username);
	}
	
	public User save(User user) {
		return this.jpaUserRepository.save(user);
	}
}
