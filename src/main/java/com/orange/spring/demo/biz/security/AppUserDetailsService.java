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

import com.orange.spring.demo.biz.persistence.model.UserDB;
import com.orange.spring.demo.biz.persistence.model.UserRoleDB;
import com.orange.spring.demo.biz.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("userDetailsService")
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<UserDB> usersDB = userRepository.findByUsername(username);
    check(usersDB, username);
    UserDB userDB = usersDB.get(0);
    List<GrantedAuthority> authorities = AppSecurityRoles.authoritiesFor(userDB.getUserRoles());
    return toSpringSecurityUser(userDB, authorities);
  }

  private void check(List<UserDB> usersDB, String username) {
    if (usersDB.isEmpty() || usersDB.size() > 1) {
      RuntimeException e = new UsernameNotFoundException(
              String.format("Found %d times user '%s'", usersDB.size(), username));
      log.warn(e.getMessage(), e);
      throw e;
    }
  }

  private User toSpringSecurityUser(UserDB userDB, List<GrantedAuthority> authorities) {
    return new User(userDB.getUsername(), userDB.getPasswordHash(), true, true, true, true, authorities);
  }
}
