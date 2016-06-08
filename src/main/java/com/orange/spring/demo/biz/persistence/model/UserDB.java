package com.orange.spring.demo.biz.persistence.model;

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

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
// default constructor only exists for the sake of JPA
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDB {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  private String username;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  private Set<UserRoleDB> userRoles = new HashSet<>();

  @NonNull
  private String passwordHash;

  public UserDB(String username, String passwordHash) {
    this.username = username;
    this.passwordHash = passwordHash;
  }
}
