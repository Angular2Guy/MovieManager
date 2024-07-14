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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.moviemanager.adapter.config.SecurityConfig;
import ch.xxx.moviemanager.domain.common.Role;
import ch.xxx.moviemanager.domain.model.dto.ActorFilterCriteriaDto;
import ch.xxx.moviemanager.domain.model.dto.SearchTermDto;
import ch.xxx.moviemanager.domain.model.entity.Actor;
import ch.xxx.moviemanager.domain.model.entity.Cast;
import ch.xxx.moviemanager.domain.model.entity.Movie;
import ch.xxx.moviemanager.domain.model.entity.User;
import ch.xxx.moviemanager.domain.utils.JwtUtils;
import ch.xxx.moviemanager.usecase.mapper.DefaultMapper;
import ch.xxx.moviemanager.usecase.service.ActorService;
import ch.xxx.moviemanager.usecase.service.JwtTokenService;
import ch.xxx.moviemanager.usecase.service.UserDetailService;
import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(controllers = ActorController.class, includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		SecurityConfig.class, JwtTokenService.class }))
@WithMockUser
public class ActorControllerTest {
	private static final String TEST_SECRECT_KEY = "w1a7WlSUrqEfDvlVd47SGlwSb9CJk1BymiIhXXZP82MyNNxsY1krgpb0bQt-Z9uohLRR6afgBsRHP_qiaHQhb"
			+ "wFNWJeTTWr1x28hABtuvbRGMdW9ihvM_8JpVDhwuFbr2YCUW_nBeqJwcT9h6024RB7gJRYdxy1R6-onq9VG-TAJ00lrsfpnWWWn7LSLoxkj4gxeLTaF_0hozjoZ"
			+"90sTm3loeS0CfX2MgXi-UAdjsGG4ki40iw4wWrKverKUtZQPotcvObtTGdAEx4DfTGdU0ZK7O9IY9xxddoGxPgG9l2_ahhPjfqMJYPY-TuI_UXiKfbFhnRTrdg8GtXyU0G3GJQ==";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActorService service;

    @MockBean
    private DefaultMapper mapper;

    @MockBean
    private UserDetailService auds;
    
	@MockBean
	private JwtTokenService jwtTokenService;
	
	private String token = "";

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void init() {
		var user = new User();
		user.setId(1L);		
		Mockito.when(this.mapper.convertOnlyActor(any(Actor.class))).thenCallRealMethod();
		Mockito.when(this.mapper.convert(any(Actor.class))).thenCallRealMethod();
		Mockito.when(this.jwtTokenService.createToken(any(String.class), any(List.class), any(Optional.class))).thenCallRealMethod();
		Mockito.when(this.jwtTokenService.validateToken(any(String.class))).thenReturn(true);
		Mockito.when(this.jwtTokenService.resolveToken(any(HttpServletRequest.class))).thenReturn(Optional.of(""));
		Mockito.when(this.jwtTokenService.getAuthentication(any(String.class))).thenCallRealMethod();		
		Mockito.when(this.jwtTokenService.getUsername(any(String.class))).thenReturn("XXX");	
		Mockito.when(this.jwtTokenService.getAuthorities(any(String.class))).thenReturn(List.of(Role.USERS));
		Mockito.when(this.auds.getCurrentUser(any(String.class))).thenReturn(user);
		ReflectionTestUtils.setField(this.jwtTokenService, "secretKey", TEST_SECRECT_KEY);
		Mockito.doCallRealMethod().when(this.jwtTokenService).init();
		this.jwtTokenService.init();
		this.token = this.jwtTokenService.createToken("XXX", List.of(Role.USERS), Optional.empty());
	}

    @Test
    public void testGetActorSearchByName() throws Exception {
        List<Actor> actors = List.of(createTestActor());        
        Mockito.when(service.findActor(any(), any())).thenReturn(actors);

        mockMvc.perform(get("/rest/actor/{name}", "testName")                
        .header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", this.token))
        .servletPath("/rest/actor/testName"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(actors.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(actors.get(0).getName()));
    }

    @Test
    public void testGetActorSearchByIdFound() throws Exception {
        Actor actor = createTestActor();
        Mockito.when(service.findActorById(any(), any())).thenReturn(Optional.of(actor));

        mockMvc.perform(get("/rest/actor/id/{id}", 1L)
        		.header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", this.token))
                .servletPath("/rest/actor/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(actor.getId()))
                .andExpect(jsonPath("$.name").value(actor.getName()));
    }

    @Test
    public void testGetActorSearchByIdNotFound() throws Exception {
        Mockito.when(service.findActorById(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/rest/actor/id/{id}", 1L)
        		.header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", this.token))
                .servletPath("/rest/actor/id/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPagesByNumber() throws Exception {
        List<Actor> actors = List.of(createTestActor());
        Mockito.when(service.findActorsByPage(any(), any())).thenReturn(actors);

        mockMvc.perform(get("/rest/actor/pages").param("page", "1")
        		.header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", this.token))
                .servletPath("/rest/actor/pages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetActorsByCriteria() throws Exception {
        List<Actor> actors = List.of(createTestActor());
        ActorFilterCriteriaDto criteria = new ActorFilterCriteriaDto();
        Mockito.when(service.findActorsByFilterCriteria(any(), any())).thenReturn(actors);

        mockMvc.perform(post("/rest/actor/filter-criteria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(criteria))
                .header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", this.token))
                .servletPath("/rest/actor/filter-criteria"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testPostSearchTerm() throws Exception {
        List<Actor> actors = List.of(createTestActor());
        SearchTermDto searchTerm = new SearchTermDto();
        Mockito.when(service.findActorsBySearchTerm(any(), any())).thenReturn(actors);

        mockMvc.perform(post("/rest/actor/searchterm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(searchTerm))
                .header(JwtUtils.AUTHORIZATION, String.format("Bearer %s", this.token))
                .servletPath("/rest/actor/searchterm"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    private Actor createTestActor() {
        Actor actor = new Actor();
        actor.setId(1L);
        actor.setName("testName");
        return actor;
    }
}