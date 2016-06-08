package com.orange.spring.demo.biz.webservice.controller;

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
import com.orange.spring.demo.biz.persistence.service.UserService;
import com.orange.spring.demo.biz.view.model.UserView;
import com.orange.spring.demo.biz.webservice.controller.model.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Types that carry this annotation are treated as controllers where @RequestMapping
 * methods assume @ResponseBody semantics by default, ie return json body.
 */
@Slf4j
@RestController
/** Rest controller: returns a json body */
public class UserController {

  @Autowired
  private UserService userService;

  @Secured("ROLE_USER")
  @RequestMapping(RestApi.WS_SEC_GET_USERS)
  public List<UserView> users() {
    return UserView.from(userService.all());
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = RestApi.WS_ADMIN_USER_CREATE, method = RequestMethod.POST)
  public void user(@RequestBody UserCredentials userCredentials, Principal principal) {
    log.info("Create user: " + userCredentials.getUsername());
    userService.create(new User(0, userCredentials.getUsername()), userCredentials.getPassword());
  }
}
