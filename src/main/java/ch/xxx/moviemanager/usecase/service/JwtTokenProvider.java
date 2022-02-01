/**
 *    Copyright 2016 Sven Loesekann

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

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import ch.xxx.moviemanager.domain.common.Role;
import ch.xxx.moviemanager.domain.exceptions.AccessUnauthorizedException;
import ch.xxx.moviemanager.domain.exceptions.JwtTokenValidationException;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.model.entity.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key}")
	private String secretKey;

	@Value("${security.jwt.token.expire-length}")
	private long validityInMilliseconds; // 24h

	private final UserRepository userRepository;
	
	public JwtTokenProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String createToken(String username, List<Role> roles) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put("auth", roles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority()))
				.filter(Objects::nonNull).collect(Collectors.toList()));

		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public Optional<Jws<Claims>> getClaims(Optional<String> token) {
		if (!token.isPresent()) {
			return Optional.empty();
		}
		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
		return Optional.of(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.get()));
	}

	public UsernamePasswordAuthenticationToken getUserAuthenticationToken(String token) {
		User user = this.userRepository.findByUsername(this.getUsername(token)).orElseThrow(() -> new AccessUnauthorizedException(this.getUsername(token)));
		
		return new UsernamePasswordAuthenticationToken(user, "", List.of(Role.USERS));
	}

	public String getUsername(String token) {
		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	public boolean validateToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtTokenValidationException("Expired or invalid JWT token", e);
		}
	}

}
