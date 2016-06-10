package com.orange.spring.demo.biz.view.model;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserView {
  public static final String EMPTY_PASSWORD = "";

  private long id;
  private String username;
  private String password;

  public User toUser() {
    return new User(id, username);
  }

  public static UserView from(User user) {
    return new UserView(user.id, user.username, EMPTY_PASSWORD);
  }

  public static List<UserView> from(List<User> users) {
    return users.stream()
            .map(UserView::from)
            .collect(Collectors.toList());
  }
}
