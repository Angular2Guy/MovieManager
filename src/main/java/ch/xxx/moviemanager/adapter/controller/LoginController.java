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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.domain.exceptions.AccessForbiddenException;
import ch.xxx.moviemanager.usecase.model.UserDto;
import ch.xxx.moviemanager.usecase.service.UserDetailsMgmtService;

@RestController
@RequestMapping("rest/user")
public class LoginController {
	private final UserDetailsMgmtService auds;	

	public LoginController(UserDetailsMgmtService auds) {
		this.auds = auds;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> postLogin(@RequestBody UserDto userDto) throws InterruptedException {
		return new ResponseEntity<Boolean>(this.auds.loginUser(userDto), HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> postSignin(@RequestBody UserDto userDto) throws InterruptedException {
		if (!this.auds.saveUser(userDto)) {
			throw new AccessForbiddenException(userDto.toString());
		}
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.ACCEPTED);
	}
	
//	@RequestMapping(value="/updateDB", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Boolean> getUpdateDB() throws InterruptedException {
//		boolean result = this.service.updateDB();
//		return new ResponseEntity<Boolean>( result, result ? HttpStatus.OK : HttpStatus.NOT_IMPLEMENTED);	
//	}
}
