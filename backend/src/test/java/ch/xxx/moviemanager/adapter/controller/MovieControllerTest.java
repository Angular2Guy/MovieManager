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

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ch.xxx.moviemanager.domain.common.Role;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.utils.JwtUtils;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.service.JwtTokenService;
import ch.xxx.moviemanager.usecase.service.MovieService;

@WebMvcTest(controllers = MovieController.class)
@WithMockUser
public class MovieControllerTest {
	private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huIiwiYXV0aCI6W3siYXV0aG9yaXR5IjoiVVNFUlMifV0sImxhc3Rtc2ciOjE2NzM5NzIyMjgxMTIsInV1aWQiOiI2N2ZhZjc1Mi0yMTUyLTQyYzMtODg4Ni1jMGQxMDY1M2Y4NDkiLCJpYXQiOjE2NzM5NzIyMjgsImV4cCI6MTY3Mzk3MjI4OH0.Y4yGhD3B5SH9swIsRrmT1vyssbGWnCOwZZLOLp7HKds";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private MovieService service;
	@MockBean
	private JwtTokenService jwtTokenService;
	@MockBean
	private DefaultMapper defaultMapper;

	@BeforeEach
	public void init() {
		Mockito.when(this.defaultMapper.convertOnlyMovie(any(Movie.class))).thenCallRealMethod();
		Mockito.when(this.defaultMapper.convert(any(Movie.class))).thenCallRealMethod();
		Mockito.when(this.jwtTokenService.resolveToken(any(HttpServletRequest.class))).thenReturn("XXX");
		Mockito.when(this.jwtTokenService.validateToken(any(String.class))).thenReturn(Boolean.TRUE);
		Mockito.when(this.jwtTokenService.getAuthentication(any(String.class)))
				.thenReturn(new UsernamePasswordAuthenticationToken("XXX", "", List.of(Role.USERS)));
	}

	@Test
	public void movieSearchByNameTest() throws Exception {
		final Movie myMovie = createTestMovieEntity();
		Mockito.when(service.findMovie(any(String.class), any(String.class))).thenReturn(List.of(myMovie));
		this.mockMvc
				.perform(get("/rest/movie/xxx").header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN))
						.servletPath("/rest/movie"))
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
		final Movie myMovie = createTestMovieEntity();
		Mockito.when(this.service.findMovieById(any(Long.class))).thenReturn(Optional.of(myMovie));
		this.mockMvc.perform(get("/rest/movie/id/1").header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN))
						.servletPath("/rest/movie"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", Matchers.is(Matchers.equalTo(myMovie.getId())), Long.class))
		.andExpect(jsonPath("$.title", Matchers.is(Matchers.equalTo(myMovie.getTitle())), String.class));
	}
	
	@Test
	public void movieSearchByIdNotFoundTest() throws Exception {
		Mockito.when(this.service.findMovieById(any(Long.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(get("/rest/movie/id/1").header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN))
						.servletPath("/rest/movie"))
		.andExpect(status().isNotFound());
	}
}
