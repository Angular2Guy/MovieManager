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
package ch.xxx.moviemanager.usecase.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DataMigrationService {
	private final UserRepository userRepository;
	private final UserDetailService userDetailService;

	public DataMigrationService(UserRepository userRepository, UserDetailService userDetailService) {
		this.userRepository = userRepository;
		this.userDetailService = userDetailService;
	}

	@Async
	public CompletableFuture<Long> encryptUserKeys() {
		List<User> migratedUsers = this.userRepository.findOpenMigrations(1L).stream().map(myUser -> updateUser(myUser))
				.collect(Collectors.toList());
		this.userRepository.saveAll(migratedUsers);
		return CompletableFuture.completedFuture(Integer.valueOf(migratedUsers.size()).longValue());
	}

	private User updateUser(User myUser) {
		myUser.setUuid(Optional.ofNullable(myUser.getUuid()).filter(Predicate.not(String::isBlank))
				.orElse(UUID.randomUUID().toString()));
		myUser.setMoviedbkey(this.userDetailService.encrypt(myUser.getMoviedbkey(), myUser.getUuid()));
		myUser.setMigration(myUser.getMigration() + 1);
		return myUser;
	}

}
