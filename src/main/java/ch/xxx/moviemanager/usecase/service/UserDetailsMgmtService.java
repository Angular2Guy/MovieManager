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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
import ch.xxx.moviemanager.domain.model.entity.RevokedToken;
import ch.xxx.moviemanager.domain.model.entity.RevokedTokenRepository;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;
import ch.xxx.moviemanager.domain.utils.TokenSubjectRole;
import ch.xxx.moviemanager.usecase.mapper.UserMapper;

@Service
@Transactional
public class UserDetailsMgmtService {
	private final static Logger LOG = LoggerFactory.getLogger(UserDetailsMgmtService.class);
	private final static long LOGOUT_TIMEOUT = 185L;
	private final UserRepository userRepository;
	private final RevokedTokenRepository revokedTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender javaMailSender;
	private final JwtTokenService jwtTokenService;
	private final UserMapper userMapper;
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
	@Value("${mail.url.uuid.confirm}")
	private String confirmUrl;

	public UserDetailsMgmtService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			RevokedTokenRepository revokedTokenRepository, JavaMailSender javaMailSender,
			JwtTokenService jwtTokenService, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.javaMailSender = javaMailSender;
		this.jwtTokenService = jwtTokenService;
		this.userMapper = userMapper;
		this.revokedTokenRepository = revokedTokenRepository;
	}

	public void updateLoggedOutUsers() {
		final List<RevokedToken> revokedTokens = new ArrayList<RevokedToken>(this.revokedTokenRepository.findAll());
		this.jwtTokenService.updateLoggedOutUsers(revokedTokens.stream()
				.filter(myRevokedToken -> myRevokedToken.getLastLogout() == null
				|| !myRevokedToken.getLastLogout().isBefore(LocalDateTime.now().minusSeconds(LOGOUT_TIMEOUT)))
				.toList());
		this.revokedTokenRepository.deleteAll(revokedTokens.stream()
				.filter(myRevokedToken -> myRevokedToken.getLastLogout() != null
						&& myRevokedToken.getLastLogout().isBefore(LocalDateTime.now().minusSeconds(LOGOUT_TIMEOUT)))
				.toList());
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
		if (!this.jwtTokenService.validateToken(this.jwtTokenService.resolveToken(bearerStr).orElse(""))) {
			throw new AuthenticationException("Invalid token.");
		}
		String username = this.jwtTokenService.getUsername(this.jwtTokenService.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
		String uuid = this.jwtTokenService.getUuid(this.jwtTokenService.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
		this.userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Username not found: " + username));
		long revokedTokensForUuid = this.revokedTokenRepository.findAll().stream()
				.filter(myRevokedToken -> myRevokedToken.getUuid().equals(uuid)
						&& myRevokedToken.getName().equalsIgnoreCase(username))
				.count();
		if (revokedTokensForUuid == 0) {
			this.revokedTokenRepository.save(new RevokedToken(username, uuid, LocalDateTime.now()));
		} else {
			LOG.warn("Duplicate logout for user {}", username);
		}
		return Boolean.TRUE;
	}

	private UserDto loginHelp(Optional<User> entityOpt, String passwd) {
		UserDto user = new UserDto();
		Optional<Role> myRole = entityOpt.stream().flatMap(myUser -> Arrays.stream(Role.values())
				.filter(role1 -> Role.USERS.equals(role1)).filter(role1 -> role1.name().equals(myUser.getRoles())))
				.findAny();
		if (myRole.isPresent() && entityOpt.get().isEnabled()
				&& this.passwordEncoder.matches(passwd, entityOpt.get().getPassword())) {
			Callable<String> callableTask = () -> this.jwtTokenService.createToken(entityOpt.get().getUsername(),
					Arrays.asList(myRole.get()), Optional.empty());
			try {
				String jwtToken = executorService.schedule(callableTask, 3, TimeUnit.SECONDS).get();
				user = this.jwtTokenService.userNameLogouts(entityOpt.get().getUsername()) > 2 ? user
						: this.userMapper.convert(entityOpt.get(), jwtToken, 0L);
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Login failed.", e);
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
