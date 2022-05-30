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

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.moviemanager.domain.model.dto.RevokedTokenDto;
import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.domain.model.entity.RevokedToken;
import ch.xxx.moviemanager.domain.model.entity.RevokedTokenRepository;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;
import ch.xxx.moviemanager.domain.producer.EventProducer;
import ch.xxx.moviemanager.usecase.mapper.RevokedTokenMapper;
import ch.xxx.moviemanager.usecase.mapper.UserMapper;

@Profile("!kafka & !prod-kafka")
@Transactional
@Service
public class UserDetailMgmtServiceEvents extends UserDetailsMgmtService {
	private static final long LOGOUT_TIMEOUT = 95L;
	private final EventProducer eventProducer;
	
	public UserDetailMgmtServiceEvents(UserRepository userRepository, PasswordEncoder passwordEncoder, RevokedTokenMapper revokedTokenMapper,
			RevokedTokenRepository revokedTokenRepository, JavaMailSender javaMailSender, EventProducer eventProducer,
			JwtTokenService jwtTokenService, UserMapper userMapper, EventProducer messageProducer) {
		super(userRepository, passwordEncoder, revokedTokenRepository, javaMailSender, jwtTokenService, userMapper, revokedTokenMapper);
		this.eventProducer = eventProducer;
	}

	@Override
	public void updateLoggedOutUsers() {
		this.updateLoggedOutUsers(LOGOUT_TIMEOUT);
	}
	
	@Override
	public Boolean signin(UserDto appUserDto) {
		Optional<User> appUserOpt = super.signin(appUserDto, false, true);
		appUserOpt.ifPresent(myAppUser -> this.eventProducer.sendNewUserMsg(this.userMapper.convert(myAppUser)));
		return appUserOpt.isPresent();
	}
	
	public Boolean signinMsg(UserDto appUserDto) {
		return super.signin(appUserDto, true, false).isPresent();
	}
	
	@Override
	public Boolean logout(String bearerStr) {
		Optional<RevokedToken> logoutTokenOpt = this.logoutToken(bearerStr);
		logoutTokenOpt.ifPresent(revokedToken -> 
			this.eventProducer.sendLogoutMsg(this.revokedTokenMapper.convert(revokedToken)));		
		return logoutTokenOpt.isPresent();
	}

	public Boolean logoutMsg(RevokedTokenDto revokedTokenDto) {
		Boolean result = super.logout(revokedTokenDto);
		this.updateLoggedOutUsers();
		return result;
	}
}
