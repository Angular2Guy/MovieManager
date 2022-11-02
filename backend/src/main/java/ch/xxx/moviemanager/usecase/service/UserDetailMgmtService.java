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

import java.util.Map;

import ch.xxx.moviemanager.domain.model.dto.KafkaEventDto;
import ch.xxx.moviemanager.domain.model.dto.RefreshTokenDto;
import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.utils.TokenSubjectRole;

public interface UserDetailMgmtService {
	User getCurrentUser(String bearerStr);
	TokenSubjectRole getTokenRoles(Map<String, String> headers);
	Boolean signin(UserDto appUserDto);
	Boolean confirmUuid(String uuid);
	UserDto login(UserDto appUserDto);
	Boolean logout(String bearerStr);
	RefreshTokenDto refreshToken(String bearerToken);
	UserDto load(Long id);
	UserDto save(UserDto appUser);
	void updateLoggedOutUsers();
	void sendKafkaEvent(KafkaEventDto kafkaEventDto);
}
