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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminTest {
  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();
  }

  @Test
  public void adminUnavailableForAll() throws Exception {
    mockMvc
            .perform(get("/admin"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  @WithMockUser
  public void adminUnavailableForUserNonAdmin() throws Exception {
    mockMvc
            .perform(get("/admin"))
            .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = AppSecurityAdmin.ADMIN_USERNAME, password = AppSecurityAdmin.ADMIN_PASSWORD, roles = "ADMIN")
  public void adminAvailableForAdmin() throws Exception {
    mockMvc
            .perform(get("/admin"))
            .andExpect(status().isOk());
  }

  @Test
  public void anonymousNotAuthorizedToCreateUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(
                    post("/admin/user/create").with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "titi")
                            .param("password", "toto")
            )
            // then
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  @WithMockUser
  public void userNotAuthorizedToCreateUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(
                    post("/admin/user/create").with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "titi")
                            .param("password", "toto")
            )
            // then
            .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = AppSecurityAdmin.ADMIN_USERNAME, password = AppSecurityAdmin.ADMIN_PASSWORD, roles = "ADMIN")
  public void adminIsAuthorizedToCreateUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(
                    post("/admin/user/create").with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "titi")
                            .param("password", "toto")
            )
            // then
            .andExpect(status().isOk());
  }
}
