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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.moviemanager.domain.common.Role;
import ch.xxx.moviemanager.domain.exceptions.AuthenticationException;
import ch.xxx.moviemanager.domain.exceptions.ResourceNotFoundException;
import ch.xxx.moviemanager.domain.model.dto.RefreshTokenDto;
import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;
import ch.xxx.moviemanager.domain.utils.TokenSubjectRole;
import ch.xxx.moviemanager.usecase.mapper.UserMapper;

@Service
@Transactional
public class UserDetailsMgmtService {
	private final static Logger LOG = LoggerFactory.getLogger(UserDetailsMgmtService.class);
	private final static long LOGIN_TIMEOUT = 245L;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender javaMailSender;
	private final JwtTokenService jwtTokenService;
	private final UserMapper userMapper;
	@Value("${mail.url.uuid.confirm}")
	private String confirmUrl;

	public UserDetailsMgmtService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JavaMailSender javaMailSender, JwtTokenService jwtTokenService, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.javaMailSender = javaMailSender;
		this.jwtTokenService = jwtTokenService;
		this.userMapper = userMapper;
	}

	public void updateLoggedOutUsers() {
		final List<User> users = this.userRepository.findLoggedOut();
		this.userRepository.saveAll(users.stream().filter(myUser -> myUser.getLastLogout() != null
				&& myUser.getLastLogout().isBefore(LocalDateTime.now().minusMinutes(2L))).map(myUser -> {
					myUser.setLastLogout(null);
					return myUser;
				}).toList());
		this.jwtTokenService.updateLoggedOutUsers(this.userRepository.findLoggedOut());
	}

	public User getCurrentUser(String bearerStr) {
		final String token = this.jwtTokenService.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string."));
		final String userName = this.jwtTokenService.getUsername(token);
		return this.userRepository.findByUsername(userName).orElseThrow(
				() -> new UsernameNotFoundException(String.format("The username %s doesn't exist", userName)));
	}

	public RefreshTokenDto refreshToken(String bearerToken) {
		Optional<String> tokenOpt = this.jwtTokenService.resolveToken(bearerToken);
		if (tokenOpt.isEmpty()) {
			throw new AuthorizationServiceException("Invalid token");
		}
		String newToken = this.jwtTokenService.refreshToken(tokenOpt.get());
		LOG.info("Jwt Token refreshed.");
		return new RefreshTokenDto(newToken);
	}

	public UserDto save(UserDto appUser) {
		return this.userMapper.convert(Optional
				.ofNullable(this.userRepository
						.save(this.userMapper.convert(appUser, this.userRepository.findById(appUser.getId()))))
				.orElseThrow(() -> new ResourceNotFoundException("User " + appUser.getId() + " not found")), "", 10L);
	}

	public Boolean signin(UserDto appUserDto) {
		return Optional.ofNullable(appUserDto.getId()).stream().flatMap(id -> Stream.of(Boolean.FALSE)).findFirst()
				.orElseGet(() -> this.checkSaveSignin(this.userMapper.convert(appUserDto,
						this.userRepository.findByUsername(appUserDto.getUsername()))));
	}

	private Boolean checkSaveSignin(User entity) {
		if (entity.getId() == null) {
			String encryptedPassword = this.passwordEncoder.encode(entity.getPassword());
			entity.setPassword(encryptedPassword);
			UUID uuid = UUID.randomUUID();
			entity.setUuid(uuid.toString());
			entity.setLocked(false);
			entity.setRoles(Role.USERS.name());
			boolean emailConfirmEnabled = this.confirmUrl != null && !this.confirmUrl.isBlank();
			entity.setEnabled(!emailConfirmEnabled);
			if (emailConfirmEnabled) {
				this.sendConfirmMail(entity);
			}
			return this.userRepository.save(entity).getId() != null;
		}
		LOG.warn("Username multiple signin: {}", entity.getUsername());
		return Boolean.FALSE;
	}

	public Boolean confirmUuid(String uuid) {
		return this.confirmUuid(this.userRepository.findByUuid(uuid), uuid);
	}

	private Boolean confirmUuid(Optional<User> entityOpt, final String uuid) {
		return entityOpt.map(entity -> {
			entity.setEnabled(true);
			return this.userRepository.save(entity).isEnabled();
		}).orElseGet(() -> {
			LOG.warn("Uuid confirm failed: {}", uuid);
			return Boolean.FALSE;
		});
	}

	public UserDto login(UserDto appUserDto) {
		return this.loginHelp(this.userRepository.findByUsername(appUserDto.getUsername()), appUserDto.getPassword());
	}

	public Boolean logout(String bearerStr) {
		String username = this.jwtTokenService.getUsername(this.jwtTokenService.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
		User user1 = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Username not found: " + username));
		user1.setLastLogout(LocalDateTime.now());
		this.userRepository.save(user1);
		return Boolean.TRUE;
	}

	private UserDto loginHelp(Optional<User> entityOpt, String passwd) {
		UserDto user = new UserDto();
		Optional<Role> myRole = entityOpt.stream().flatMap(myUser -> Arrays.stream(Role.values())
				.filter(role1 -> Role.USERS.equals(role1)).filter(role1 -> role1.name().equals(myUser.getRoles())))
				.findAny();
		if (myRole.isPresent() && entityOpt.get().isEnabled()) {			
			if (entityOpt.get().getLastLogout() == null 
					&& this.passwordEncoder.matches(passwd, entityOpt.get().getPassword())) {
				String jwtToken = this.jwtTokenService.createToken(entityOpt.get().getUsername(),
						Arrays.asList(myRole.get()), Optional.empty());
				entityOpt.get().setLastLogout(null);
				user = this.userMapper.convert(entityOpt.get(), jwtToken, 0L);
			} else if (this.passwordEncoder.matches(passwd, entityOpt.get().getPassword())) {
				Instant now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
				Instant lastLogout = entityOpt.get().getLastLogout() == null ? now.minusSeconds(LOGIN_TIMEOUT)
						: entityOpt.get().getLastLogout().atZone(ZoneId.systemDefault()).toInstant();
				Duration sinceLastLogout = Duration.between(lastLogout, now);
				user.setSecUntilNexLogin(LOGIN_TIMEOUT - sinceLastLogout.getSeconds());
			}
		}
		return user;
	}

	private void sendConfirmMail(User entity) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(entity.getEmailAddress());
		msg.setSubject("AngularPortfolioMgr Account Confirmation Mail");
		String url = this.confirmUrl + "/" + entity.getUuid();
		msg.setText(String
				.format("Welcome to the AngularPwaMessenger please use this link(%s) to confirm your account.", url));
		this.javaMailSender.send(msg);
		LOG.info("Confirm Mail send to: " + entity.getEmailAddress());
	}

	public TokenSubjectRole getTokenRoles(Map<String, String> headers) {
		return this.jwtTokenService.getTokenUserRoles(headers);
	}

	public UserDto load(Long id) {
		return this.userMapper.convert(this.userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found")), "", 10);
	}

	/*
	 * public List<UserDto> loadAll() { return
	 * this.userRepository.findAll().stream() .flatMap(entity ->
	 * Stream.of(this.appUserMapper.convert(Optional.of(entity))))
	 * .collect(Collectors.toList()); }
	 */
}
