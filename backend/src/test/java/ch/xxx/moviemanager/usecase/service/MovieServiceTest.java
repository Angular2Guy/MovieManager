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

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.xxx.moviemanager.domain.client.MovieDbRestClient;
import ch.xxx.moviemanager.domain.model.entity.ActorRepository;
import ch.xxx.moviemanager.domain.model.entity.CastRepository;
import ch.xxx.moviemanager.domain.model.entity.GenereRepository;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;

@ExtendWith(SpringExtension.class)
public class MovieServiceTest {
	@Mock
	private MovieRepository movieRep;
	@Mock
	private CastRepository castRep;
	@Mock
	private ActorRepository actorRep;
	@Mock
	private GenereRepository genereRep;
	@Mock
	private UserDetailService userDetailService;
	@Mock
	private DefaultMapper mapper;
	@Mock
	private MovieDbRestClient movieDbRestClient;
	@InjectMocks
	private MovieService movieService;
	
	@Test
	public void findMoviesByName() throws Exception {
		final Movie myMovie = this.createTestMovieEntity();		
		Mockito.when(this.movieRep.findByTitle(any(String.class), any(Long.class), any(Pageable.class))).thenReturn(List.of(myMovie));
		Mockito.when(this.userDetailService.getCurrentUser(any(String.class))).thenReturn(createTestUserEntity());
		List<Movie> myMovies = this.movieService.findMovie("XXX", "YYY");
		Assertions.assertNotNull(myMovies);
		Assertions.assertEquals(myMovies.get(0).getId(), myMovie.getId());
	}

	@Test
	public void findMovieById() throws Exception {
		final Movie myMovie = this.createTestMovieEntity();
		Mockito.when(this.movieRep.findById(any(Long.class))).thenReturn(Optional.of(myMovie));
		final User myUser = new User();
		myUser.setId(1L);
		Mockito.when(this.userDetailService.getCurrentUser(any(String.class))).thenReturn(myUser);
		Optional<Movie> movieOpt = this.movieService.findMovieById(1L, "");
		Assertions.assertTrue(movieOpt.isPresent());
		Assertions.assertEquals(movieOpt.get().getId(), myMovie.getId());
	}
	
	private User createTestUserEntity() {
		final User myUser = new User();
		myUser.setId(1L);
		return myUser;
	}
	
	private Movie createTestMovieEntity() {
		final Movie myMovie = new Movie();
		myMovie.setId(1L);
		myMovie.setTitle("myTitle");
		var user = new User();
		user.setId(1L);
		myMovie.getUsers().add(user);
		return myMovie;
	}
}
