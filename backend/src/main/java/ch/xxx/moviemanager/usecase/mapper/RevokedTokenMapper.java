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

import org.springframework.stereotype.Component;

import ch.xxx.moviemanager.domain.model.dto.RevokedTokenDto;
import ch.xxx.moviemanager.domain.model.entity.RevokedToken;


@Component
public class RevokedTokenMapper {

	public RevokedToken convert(RevokedTokenDto dto) {
		return new RevokedToken(dto.getName(), dto.getUuid(), dto.getLastLogout());
	}
	
	public RevokedTokenDto convert(RevokedToken entity) {
		return new RevokedTokenDto(entity.getName(), entity.getUuid(), entity.getLastLogout());
	}
}