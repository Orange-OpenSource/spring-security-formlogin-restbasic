package com.orange.spring.demo.biz.view.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class HomeController {
  public static final String HOME_TITLE = "Welcome";

  @Autowired
  private UserService userService;

  @RequestMapping("/")
  public String index(Principal principal, Model model) {
    setAuthenticated(principal, model);
    model.addAttribute("title", HOME_TITLE);
    return "index";
  }

  @Secured("ROLE_USER")
  @RequestMapping("/users")
  public String index(Model model) {
    setAuthenticated(true, model);
    model.addAttribute("title", "Users");
    model.addAttribute("users", UserView.from(userService.all()));
    return "users";
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/user/{id}")
  public String user(@PathVariable long id, Model model) {
    User user = userService.withId(id);
    setAuthenticated(true, model);
    model.addAttribute("title", "User details");
    model.addAttribute("user", user);
    return "user";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping("/admin")
  public String admin(Model model) {
    setAuthenticated(true, model);
    model.addAttribute("title", "Admin page");
    // for thymeleaf form management
    model.addAttribute("user", new UserView());
    return "admin";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
  public String user(@ModelAttribute UserView userView, Model model) {
    User user = userService.create(userView.toUser(), userView.getPassword());
    return user(user.id, model);
  }

  private void setAuthenticated(Principal principal, Model model) {
    boolean authenticated = principal != null && principal.getName() != null;
    setAuthenticated(authenticated, model);
    model.addAttribute("authenticatedUsername",
            authenticated ? principal.getName() : "Please sign in");
    model.addAttribute("isAdmin", authenticated && isAdmin(principal));
  }

  private void setAuthenticated(boolean isAuthenticated, Model model) {
    model.addAttribute("isAuthenticated", isAuthenticated);
  }

  private boolean isAdmin(Principal principal) {
    return ((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream()
            .filter(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
            .count() > 0;
  }
}
