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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import ch.xxx.moviemanager.adapter.config.SecurityConfig;
import ch.xxx.moviemanager.domain.common.Role;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.utils.JwtUtils;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.service.JwtTokenService;
import ch.xxx.moviemanager.usecase.service.MovieService;
import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(controllers = MovieController.class, includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		SecurityConfig.class, JwtTokenService.class }))
@WithMockUser
public class MovieControllerTest {
	private static final String TEST_SECRECT_KEY = "w1a7WlSUrqEfDvlVd47SGlwSb9CJk1BymiIhXXZP82MyNNxsY1krgpb0bQt-Z9uohLRR6afgBsRHP_qiaHQhb"
			+ "wFNWJeTTWr1x28hABtuvbRGMdW9ihvM_8JpVDhwuFbr2YCUW_nBeqJwcT9h6024RB7gJRYdxy1R6-onq9VG-TAJ00lrsfpnWWWn7LSLoxkj4gxeLTaF_0hozjoZ"
			+"90sTm3loeS0CfX2MgXi-UAdjsGG4ki40iw4wWrKverKUtZQPotcvObtTGdAEx4DfTGdU0ZK7O9IY9xxddoGxPgG9l2_ahhPjfqMJYPY-TuI_UXiKfbFhnRTrdg8GtXyU0G3GJQ==";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private MovieService service;
	@MockBean
	private JwtTokenService jwtTokenService;
	@MockBean
	private DefaultMapper defaultMapper;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void init() {
		Mockito.when(this.defaultMapper.convertOnlyMovie(any(Movie.class))).thenCallRealMethod();
		Mockito.when(this.defaultMapper.convert(any(Movie.class))).thenCallRealMethod();
		Mockito.when(this.jwtTokenService.createToken(any(String.class), any(List.class), any(Optional.class))).thenCallRealMethod();
		Mockito.when(this.jwtTokenService.validateToken(any(String.class))).thenReturn(true);
		Mockito.when(this.jwtTokenService.resolveToken(any(HttpServletRequest.class))).thenReturn(Optional.of(""));
		Mockito.when(this.jwtTokenService.getAuthentication(any(String.class))).thenCallRealMethod();		
		Mockito.when(this.jwtTokenService.getUsername(any(String.class))).thenReturn("XXX");	
		Mockito.when(this.jwtTokenService.getAuthorities(any(String.class))).thenReturn(List.of(Role.USERS));
		ReflectionTestUtils.setField(this.jwtTokenService, "secretKey", TEST_SECRECT_KEY);
		Mockito.doCallRealMethod().when(this.jwtTokenService).init();
		this.jwtTokenService.init();
	}

	@Test
	public void movieSearchByNameTest() throws Exception {
		final String TOKEN = this.jwtTokenService.createToken("XXX", List.of(Role.USERS), Optional.empty());
		Movie myMovie = createTestMovieEntity();
		Mockito.when(service.findMovie(any(String.class), any(String.class))).thenReturn(List.of(myMovie));
		this.mockMvc
				.perform(get("/rest/movie/xxx").header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN))
						.servletPath("/rest/movie/xxx"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", Matchers.is(Matchers.equalTo(myMovie.getId())), Long.class))
				.andExpect(jsonPath("$[0].title", Matchers.is(Matchers.equalTo(myMovie.getTitle())), String.class));
	}

	private Movie createTestMovieEntity() {
		final Movie myMovie = new Movie();
		myMovie.setId(1L);
		myMovie.setTitle("myTitle");
		return myMovie;
	}

	@Test
	public void movieSearchByIdFoundTest() throws Exception {
		final String TOKEN = this.jwtTokenService.createToken("XXX", List.of(Role.USERS), Optional.empty());
		Movie myMovie = createTestMovieEntity();
		Mockito.when(this.service.findMovieById(any(Long.class), any(String.class))).thenReturn(Optional.of(myMovie));
		this.mockMvc
				.perform(get("/rest/movie/id/1").header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN))
						.servletPath("/rest/movie/id/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", Matchers.is(Matchers.equalTo(myMovie.getId())), Long.class))
				.andExpect(jsonPath("$.title", Matchers.is(Matchers.equalTo(myMovie.getTitle())), String.class));
	}

	@Test
	public void movieSearchByIdNotFoundTest() throws Exception {
		final String TOKEN = this.jwtTokenService.createToken("XXX", List.of(Role.USERS), Optional.empty());
		Mockito.when(this.service.findMovieById(any(Long.class), any(String.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(get("/rest/movie/id/1").header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN))
				.servletPath("/rest/movie/id/1")).andExpect(status().isNotFound());
	}
}
