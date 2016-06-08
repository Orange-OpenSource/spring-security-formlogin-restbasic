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

import com.orange.spring.demo.biz.persistence.model.UserRoleDB;
import com.orange.spring.demo.biz.persistence.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orange.spring.demo.biz.security.AppSecurityRoles.Role.ROLE_ADMIN;
import static com.orange.spring.demo.biz.security.AppSecurityRoles.Role.ROLE_USER;

@Slf4j
@Component
public class AppSecurityRoles {

  public enum Role { ROLE_USER, ROLE_ADMIN }

  @Autowired
  private UserRoleRepository userRoleRepository;

  @PostConstruct
  void init() {
    addRoles();
  }

  private void addRoles() {
    if (userRoleRepository.count() == 0) {
      log.info("Add user roles");
      userRoleRepository.save(
              Arrays.asList(new UserRoleDB[] {
                      new UserRoleDB(ROLE_USER), new UserRoleDB(ROLE_ADMIN)
              })
      );
    }
  }

  public static List<GrantedAuthority> authorities() {
    return Arrays.stream(Role.values())
            .map(userRole -> new SimpleGrantedAuthority(userRole.toString()))
            .collect(Collectors.toList());
  }

  public static List<GrantedAuthority> authoritiesFor(Set<UserRoleDB> roles) {
    return roles.stream()
            .map(userRole -> new SimpleGrantedAuthority(userRole.toString()))
            .collect(Collectors.toList());
  }
}
