package com.orange.spring.demo.biz.security;

/*
 * #%L
 * Spring demo
 * %%
 * Copyright (C) 2016 Orange
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.spring.demo.biz.domain.User;
import com.orange.spring.demo.biz.persistence.service.UserService;
import com.orange.spring.demo.biz.webservice.controller.RestApi;
import com.orange.spring.demo.biz.webservice.controller.model.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiSecurityTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserService userService;

  private MockMvc mockMvc;

  private String username = "roro";
  private String password = "rara";

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();

    if (shouldCreateUser()) {
      userService.create(new User(0, username), password);
    }
  }

  private boolean shouldCreateUser() {
    return userService.all().stream()
            .filter(u -> u.username.equals(username))
            .count() == 0;
  }

  @Test
  public void retrieveUsersAsAnAuthenticatedUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(MockMvcRequestBuilders.get(RestApi.WS_SEC_GET_USERS)
                            .with(httpBasic(username, password))
                            .accept(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk());
  }

  @Test
  public void failedToRetrieveUsersAsAnAuthenticatedUser() throws Exception {
    mockMvc
            // do
            .perform(get(RestApi.WS_SEC_GET_USERS)
                    .accept(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isUnauthorized());
  }

  @Test
  public void notAuthorizedToCreateUserWithoutAuth() throws Exception {
    // given
    mockMvc
            // do
            .perform(post(RestApi.WS_ADMIN_USER_CREATE))
            // then
            .andExpect(status().isUnauthorized());
  }


  @Test
  public void forbiddenToCreateUserWithBasicAuthAsANonAdminUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(post(RestApi.WS_ADMIN_USER_CREATE).with(httpBasic(username, password))
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyForUserCreation()))
            // then
            .andExpect(status().isForbidden());
  }

  @Test
  public void authorizedToCreateUserWithBasicAuth() throws Exception {
    // given
    mockMvc
            // do
            .perform(
                    post(RestApi.WS_ADMIN_USER_CREATE).with(httpBasic(AppSecurityAdmin.ADMIN_USERNAME, AppSecurityAdmin.ADMIN_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyForUserCreation())
            )
            // then
            .andExpect(status().isOk());
  }

  private String bodyForUserCreation() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(new UserCredentials("titi", "toto"));
  }
}
