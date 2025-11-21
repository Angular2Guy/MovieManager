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
package ch.xxx.moviemanager.adapter.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.MovieRepository;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MovieRepositoryTest {
	@Autowired
	private MovieRepository movieRepository;
	
	@Test
	public void findByIdFound() throws Exception {
		Long myId = 1L;
		Optional<Movie> movieOpt = this.movieRepository.findById(myId);
		Assertions.assertTrue(movieOpt.isPresent());
		Assertions.assertEquals(myId, movieOpt.get().getId());
	}
	
	@Test
	public void findByIdNotFound() throws Exception {
		Long myId = -1L;
		Optional<Movie> movieOpt = this.movieRepository.findById(myId);
		Assertions.assertTrue(movieOpt.isEmpty());		
	}
	
	@Test
	public void findByTitleFound() throws Exception {
		String title = "Test Movie Title";
		Long userId = 1L;
		PageRequest pageRequest = PageRequest.of(0, 15, Sort.by("title").ascending());
		List<Movie> myMovies = this.movieRepository.findByTitle(title, userId, pageRequest);
		Assertions.assertFalse(myMovies.isEmpty());
		Assertions.assertEquals(title, myMovies.get(0).getTitle());
	}
	
	@Test
	public void findByTitleNotFound() throws Exception {
		String title = "XYZ";
		Long userId = 1L;
		PageRequest pageRequest = PageRequest.of(0, 15, Sort.by("title").ascending());
		List<Movie> myMovies = this.movieRepository.findByTitle(title, userId, pageRequest);
		Assertions.assertTrue(myMovies.isEmpty());
	}
}
