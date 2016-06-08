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

import com.orange.spring.demo.biz.webservice.controller.RestApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
// order 1 since we have to check the REST api first
@Order(1)
/** Security config for the REST API, for non browser clients */
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    disableCsrfForNonBrowserApi(http);
    initApi(http);
  }

  private void initApi(HttpSecurity http) throws Exception {
    http
            // configure the HttpSecurity to only be invoked when matching the provided ant pattern
            .antMatcher(RestApi.WS_ROOT + "**")
            // configure restricting access
            .authorizeRequests()
            // open api is... opened
            .antMatchers(RestApi.WS_OPEN + "**").permitAll()
            // admin api restricted to... ADMIN
            .antMatchers(RestApi.WS_ADMIN + "**").hasRole("ADMIN")
            // and the rest is allowed by any authenticated user
            .antMatchers(RestApi.WS_SEC + "**").authenticated()
            .and()
            .httpBasic();
  }

  /** see http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#when-to-use-csrf-protection */
  private void disableCsrfForNonBrowserApi(HttpSecurity http) throws Exception {
    http.csrf().disable();
  }
}
