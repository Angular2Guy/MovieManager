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
package ch.xxx.moviemanager.adapter.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ch.xxx.moviemanager.usecase.service.JwtTokenService;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SecurityConfig {
	private final JwtTokenService jwtTokenService;

	public SecurityConfig(JwtTokenService jwtTokenService) {
		this.jwtTokenService = jwtTokenService;
	}

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		JwtTokenFilter customFilter = new JwtTokenFilter(jwtTokenService);
		HttpSecurity httpSecurity = http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/rest/auth/**").permitAll()
				.requestMatchers("/rest/**").authenticated().requestMatchers("/**").permitAll().anyRequest()).csrf()
				.disable().headers()
				.contentSecurityPolicy(
						"default-src 'self' data:; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';")
				.and().xssProtection().and().frameOptions().sameOrigin().and()
				.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
