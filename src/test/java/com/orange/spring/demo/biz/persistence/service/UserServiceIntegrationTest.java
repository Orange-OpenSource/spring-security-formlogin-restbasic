package com.orange.spring.demo.biz.persistence.service;

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

import com.orange.spring.demo.biz.domain.User;
import com.orange.spring.demo.biz.persistence.model.UserDB;
import com.orange.spring.demo.biz.persistence.model.UserRoleDB;
import com.orange.spring.demo.biz.persistence.repository.UserRepository;
import com.orange.spring.demo.biz.security.AppSecurityRoles;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private long id = 1234;
  private String username = "Duchess";
  private String password = "aristocats";

  @Test
  public void createUser() {
    // given
    // do
    User user = userService.create(new User(id, username), password);

    UserDB userDB = userRepository.findOne(user.id);
    String passwordHash = userDB.getPasswordHash();
    Set<UserRoleDB> roles = userDB.getUserRoles();

    // then
    Assertions.assertThat(user.username).isEqualTo(username);
    Assertions.assertThat(passwordEncoder.matches(password, passwordHash)).isTrue();
    Assertions.assertThat(passwordEncoder.matches("", passwordHash)).isFalse();
    Assertions.assertThat(roles).hasSize(1);
    Assertions.assertThat(roles.stream().findFirst().get().getRole()).isEqualTo(AppSecurityRoles.Role.ROLE_USER.toString());
  }
}
