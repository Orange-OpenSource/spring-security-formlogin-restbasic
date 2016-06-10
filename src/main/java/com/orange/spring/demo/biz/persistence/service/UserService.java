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
import com.orange.spring.demo.biz.persistence.repository.UserRoleRepository;
import com.orange.spring.demo.biz.security.AppSecurityRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;

  public List<User> all() {
    return usersFrom(userRepository.findAll());
  }

  public User withId(long id) {
    return userFrom(userRepository.findOne(id));
  }

  public User create(User user, String password) {
    UserDB userDB = userRepository.save(userDBFrom(user, password));
    return userFrom(userDB);
  }

  private List<User> usersFrom(Iterable<UserDB> usersDB) {
    List<User> users = new ArrayList<>();
    usersDB.forEach(userDB -> users.add(userFrom(userDB)));
    return users;
  }

  private User userFrom(UserDB userDB) {
    return new User(userDB.getId(), userDB.getUsername());
  }

  /**
   * Create a transient UserDB with a hashed password and a ROLE_USER as default
   * @param user user domain object
   * @param password raw password
   * @return the UserDB object to persist
   */
  private UserDB userDBFrom(User user, String password) {
    UserDB userDB = new UserDB(user.username, passwordEncoder.encode(password));
    addUserRole(userDB);
    return userDB;
  }

  private void addUserRole(UserDB userDB) {
    userDB.getUserRoles().add(
            userRoleRepository.findByRole(AppSecurityRoles.Role.ROLE_USER.toString()).get(0)
    );
  }
}
