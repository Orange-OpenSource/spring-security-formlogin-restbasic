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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

@Slf4j
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private Environment environment;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    disableSecurityOnWebJars(http);
    disableSecForDBConsole(http);

    http
            // configure the HttpSecurity to only be invoked when matching the provided ant pattern
            .antMatcher("/**")
            // configure restricting access
            .authorizeRequests()
            // open api is... opened
            .antMatchers("/").permitAll()
            // admin api restricted to... ADMIN
            .antMatchers("/admin/**").hasRole("ADMIN")
            // and the rest is allowed by any authenticated user
            .anyRequest().authenticated()
            .and()
            // setup login & logout
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("/")
            .permitAll();
  }

  private void disableSecurityOnWebJars(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/webjars/**").permitAll();
  }

  private void disableSecForDBConsole(HttpSecurity http) throws Exception {
    if (isDevProfile()) {
      log.warn("Disable security to allow H2 console");
      String url = "/h2-console/**";
      http.csrf().ignoringAntMatchers(url);
      http.authorizeRequests().antMatchers(url).permitAll();
      http.headers().frameOptions().disable();
    }
  }

  private boolean isDevProfile() {
    return Arrays.asList(environment.getActiveProfiles()).contains("dev");
  }
}
