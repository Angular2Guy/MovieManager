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
package ch.xxx.moviemanager.adapter.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.domain.common.Role;
import ch.xxx.moviemanager.domain.model.dto.AuthCheckDto;
import ch.xxx.moviemanager.domain.model.dto.KafkaEventDto;
import ch.xxx.moviemanager.domain.model.dto.RefreshTokenDto;
import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.usecase.service.UserDetailMgmtService;

@RestController
@RequestMapping("rest/auth")
public class AuthenticationController {
	private final UserDetailMgmtService auds;	
	@Value("${spring.mail.username}")
	private String mailuser;
	@Value("${spring.mail.password}")
	private String mailpwd;
	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public AuthenticationController(UserDetailMgmtService auds) {
		this.auds = auds;
	}

	@PostMapping("/authorize")
	public AuthCheckDto postAuthorize(@RequestBody AuthCheckDto authcheck, @RequestHeader Map<String, String> header) {
		String tokenRoles = this.auds.getTokenRoles(header).role();
		if (tokenRoles != null && tokenRoles.contains(Role.USERS.name()) && !tokenRoles.contains(Role.GUEST.name())) {
			return new AuthCheckDto(authcheck.getPath(), true);
		} else {
			return new AuthCheckDto(authcheck.getPath(), false);
		}
	}
	
	@PostMapping("/signin")
	public Boolean postUserSignin(@RequestBody UserDto myUser) {
		return this.auds.signin(myUser);
	}
	
	@GetMapping("/confirm/{uuid}")
	public Boolean getConfirmUuid(@PathVariable String uuid) {
		return this.auds.confirmUuid(uuid);
	}

	@PostMapping("/login")
	public UserDto postUserLogin(@RequestBody UserDto myUser) {
		return this.auds.login(myUser);
	}
	
	@PutMapping("/logout")
	public Boolean putUserLogout(@RequestHeader(value =  HttpHeaders.AUTHORIZATION) String bearerStr) {
		return this.auds.logout(bearerStr);
	}
	
	@GetMapping("/refreshToken")
	public RefreshTokenDto getRefreshToken(@RequestHeader(value =  HttpHeaders.AUTHORIZATION) String bearerStr) {
		return this.auds.refreshToken(bearerStr);
	}
	
	@GetMapping("/id/{id}")
	public UserDto getUser(@PathVariable Long id) {
		return this.auds.load(id);
	}
	
	@PutMapping()
	public UserDto putUser(@RequestBody UserDto appUserDto) {
		return this.auds.save(appUserDto);
	}
	
	@PutMapping("/kafkaEvent")
	public ResponseEntity<Boolean> putKafkaEvent(@RequestBody KafkaEventDto dto) {
		Boolean result = Boolean.TRUE;
		if(!this.activeProfile.toLowerCase().contains("prod")) {
			this.auds.sendKafkaEvent(dto);
			new ResponseEntity<Boolean>(result, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<Boolean>(result, HttpStatus.FORBIDDEN);
	}
	
//	@RequestMapping(value="/updateDB", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Boolean> getUpdateDB() throws InterruptedException {
//		boolean result = this.service.updateDB();
//		return new ResponseEntity<Boolean>( result, result ? HttpStatus.OK : HttpStatus.NOT_IMPLEMENTED);	
//	}
}
