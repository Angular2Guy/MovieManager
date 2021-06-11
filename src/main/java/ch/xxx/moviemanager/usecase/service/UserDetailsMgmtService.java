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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.moviemanager.domain.exceptions.AccessForbiddenException;
import ch.xxx.moviemanager.domain.exceptions.AccessUnauthorizedException;
import ch.xxx.moviemanager.domain.model.User;
import ch.xxx.moviemanager.domain.model.UserRepository;
import ch.xxx.moviemanager.usecase.model.UserDto;

@Service
@Transactional
public class UserDetailsMgmtService implements UserDetailsService {
	private final List<GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority("ROLE_USER"));
	private final UserRepository userRepository;

	public UserDetailsMgmtService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("The username %s doesn't exist", username)));
		List<SimpleGrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
				.map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), authorities);
		return userDetails;
	}

	public boolean loginUser(UserDto userDto) {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		try {
			UserDetails userDetails = this.loadUserByUsername(userDto.getUsername());
			if (userDto.getUsername().equals(userDetails.getUsername())
					&& encoder.matches(userDto.getPassword(), userDetails.getPassword())) {
				Authentication result1 = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
						userDetails.getPassword(), AUTHORITIES);
				SecurityContextHolder.getContext().setAuthentication(result1);
				return true;
			}
		} catch (UsernameNotFoundException e) {
			throw new AccessForbiddenException(userDto.toString());
		}
		throw new AccessUnauthorizedException(userDto.toString());
	}

	public User getCurrentUser() {
		final String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		return this.userRepository.findByUsername(userName).orElseThrow(
				() -> new UsernameNotFoundException(String.format("The username %s doesn't exist", userName)));
	}

	public boolean saveUser(UserDto userDto) {
		if(this.userRepository.findByUsername(userDto.getUsername()).isEmpty()) {
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			User user = new User();
			user.setMoviedbkey(userDto.getMoviedbkey());
			user.setPassword(encoder.encode(userDto.getPassword()));
			user.setRoles("ROLE_USER");
			user.setUsername(userDto.getUsername());
			this.userRepository.save(user);
			return true;
		}
		return false;
	}
}
