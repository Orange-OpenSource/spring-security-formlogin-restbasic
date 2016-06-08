package com.orange.spring.demo.biz.persistence.repository;

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

import com.orange.spring.demo.biz.persistence.model.UserDB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  private String username1 = "Duchess";
  private String password1 = "1234";

  private String username2 = "Thomas";
  private String password2 = "4321";

  @Test
  public void returnAllPersisted() throws IOException {
    // given
    entityManager.persist(new UserDB(username1, password1));
    entityManager.persist(new UserDB(username2, password2));
    // do
    Iterable<UserDB> users = userRepository.findAll();
    UserDB user1 = userRepository.findByUsername(username1).get(0);
    UserDB user2 = userRepository.findByUsername(username2).get(0);
    // then
    assertThat(users).hasSize(2);
    assertThat(users).contains(user1);
    assertThat(users).contains(user2);

    assertThat(user1.getUsername()).isEqualTo(username1);
    assertThat(user1.getPasswordHash()).isEqualTo(password1);

    assertThat(user2.getUsername()).isEqualTo(username2);
    assertThat(user2.getPasswordHash()).isEqualTo(password2);
  }

  @Test
  public void createUser() {
    // given
    // do
    userRepository.save(new UserDB(username1, password1));
    UserDB user1 = userRepository.findByUsername(username1).get(0);
    // then
    assertThat(user1.getUsername()).isEqualTo(username1);
    assertThat(user1.getPasswordHash()).isEqualTo(password1);
  }
}
