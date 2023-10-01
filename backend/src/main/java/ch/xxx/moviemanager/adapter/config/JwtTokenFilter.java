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
package ch.xxx.moviemanager.adapter.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import ch.xxx.moviemanager.usecase.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenFilter extends GenericFilterBean {

	private JwtTokenService jwtTokenProvider;

	public JwtTokenFilter(JwtTokenService jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {

		Optional<String> tokenOpt = jwtTokenProvider.resolveToken((HttpServletRequest) req);
		tokenOpt.stream().filter(myToken -> jwtTokenProvider.validateToken(myToken)).findFirst().ifPresentOrElse(
				token -> SecurityContextHolder.getContext()
						.setAuthentication(jwtTokenProvider.getAuthentication(token)),
				() -> SecurityContextHolder.getContext().setAuthentication(null));

		filterChain.doFilter(req, res);
	}

}
