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

public class RestApi {
  public static final String WS_ROOT = "/ws/";
  public static final String WS_OPEN = WS_ROOT + "open/";
  public static final String WS_SEC = WS_ROOT + "sec/";
  public static final String WS_ADMIN = WS_ROOT + "admin/";

  public static final String WS_SEC_GET_USERS = WS_SEC + "users";
  public static final String WS_ADMIN_USER_CREATE = WS_ADMIN + "user/create";
}
