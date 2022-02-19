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
package ch.xxx.moviemanager.usecase.mapper;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.domain.model.entity.User;

@Component
public class UserMapper {
	public UserDto convert(User user, String token) {
		UserDto dto = new UserDto(user.getId(), user.getUsername(), "XXX",
				"YYY", user.getRoles(), token, "ZZZ", "AAA", LocalDate.EPOCH);
		return dto;
	}
	
	public User convert(UserDto dto, Optional<User> entityOpt) {
		final User myEntity = entityOpt.orElse(new User());
		myEntity.setBirthDate(dto.getBirthDate());
		myEntity.setEmailAddress(dto.getEmailAddress());
		myEntity.setPassword(dto.getPassword());
		myEntity.setUsername(dto.getUsername());
		myEntity.setRoles(dto.getRoles());
		myEntity.setUuid(dto.getUuid());		
		return myEntity;
	}
}
