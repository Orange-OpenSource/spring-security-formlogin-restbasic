[![Build Status](https://travis-ci.org/Orange-OpenSource/spring-security-formlogin-restbasic.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/spring-security-formlogin-restbasic)
[![License](https://img.shields.io/aur/license/yaourt.svg?maxAge=2592000)](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/LICENSE.TXT)
[![Website](https://img.shields.io/website-up-down-green-red/http/shields.io.svg)](http://springsecmvcrestbasic1-cmorange.rhcloud.com/)


# Spring security demo app
 - spring security with form login AND secured REST api with http basic auth
 - in memory authent for admin user AND jpa/hibernate persistence for users
 - thymeleaf templates for the ui
 - tests: security is tested for all html pages and the REST api
 - use of a domain model

## Try
Openshift instance is [here](http://springsecmvcrestbasic1-cmorange.rhcloud.com/)

## Build then Run
 - ```./mvnw clean install```
 - ```./mvnw spring-boot:run```
 - open your browser at [http://localhost:8080](http://localhost:8080)

# Description

## Security
Security stuff are gathered under the ```com.orange.spring.demo.security``` package.
We rely on annotations to configure the application, there is no xml configuration.
I am not saying that we should not use xml configuration, just that it is not the case here.  

### enable security, password hash and admin account
In the [GlobalSecurityConfig](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/GlobalSecurityConfig.java) class, we do the following things:
 - we annotate the class with ```@EnableWebSecurity``` to enable spring security
 - we configure a password encoder ```@Bean``` for user passwords hashing. Then spring security can use this bean to check passwords for us, and we can use it as well when we create a new user.
 - we setup the admin account "in memory". This way it is easier to start, but the drawback of this approach is that you need to update the application when you want to add admin users or update their passwords. For users we rely on a database.

So it looks like this:
```java
@EnableWebSecurity
public class GlobalSecurityConfig {

  @Autowired
  @Qualifier("userDetailsService")
  private UserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    AppSecurityAdmin.addAdminInMemory(auth);
  }
}
```

And for [AppSecurityAdmin](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/AppSecurityAdmin.java):
```java
public class AppSecurityAdmin {

  public static void addAdminInMemory(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
            .withUser("admin")
            .password("admin password")
            .authorities(AppSecurityRoles.authorities());
  }
}
```

There are three security levels in the application:
 - anonymous
 - authenticated user: 'ROLE_USER'
 - admin: 'ROLE_ADMIN'
 
These roles are configured at start up in [AppSecurityRoles](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/AppSecurityRoles.java). First we check if the roles have been created in the DB, and if not we create it.
```java
  ...
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
  ...
```

## Distinct security authentication mechanisms for form login and REST api
To authenticate users, we can rely on several mechanisms:
 - session based: we create a new session after the user has successfully signed in through the login page. Then the session id present in http requests headers will let us do authentication.
 - token based: OAuth, json web token, etc are another mean, typically for stateless applications. See [here](https://jwt.io/introduction/) and a spring demo [here](https://github.com/szerhusenBC/jwt-spring-security-demo) 
 - http basic: the login and password of the user are encoded in the header of http requests. See [here](https://en.wikipedia.org/wiki/Basic_access_authentication).

On this application we want to use:
 - regular session based authentication for form login / html pages
 - http basic authentication for the REST api
 
To do this we have defined two distinct ```WebSecurityConfigurerAdapter``` [doc](http://docs.spring.io/spring-security/site/docs/current/apidocs/org/springframework/security/config/annotation/web/configuration/WebSecurityConfigurerAdapter.html):
 - the [ApiSecurityConfig](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/ApiSecurityConfig.java) class which will configure the security of the REST api
 - the [BrowserSecurityConfig](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/BrowserSecurityConfig.java) class which will configure the security of the app when used with a browser

Both classes extends ```WebSecurityConfigurerAdapter``` and override the ```configure```method to configure the ```HttpSecurity``` instance.

Here we need to define a priority: in the security chain pipeline, which configurer will be applied first? the problem is solved in the [ApiSecurityConfig](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/ApiSecurityConfig.java) class with the ```@Order(1)``` annotation: the REST api configurer will be applied first.

## Secure the REST api
It is done in the [ApiSecurityConfig](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/ApiSecurityConfig.java) class:
```java
@Configuration
@Order(1)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    disableCsrfForNonBrowserApi(http);
    initApi(http);
  }

  private void initApi(HttpSecurity http) throws Exception {
    http
            // configure the HttpSecurity to only be invoked when matching the provided ant pattern
            .antMatcher("/ws/**")
            // configure restricting access
            .authorizeRequests()
            // open api is... opened
            .antMatchers("/ws/open/**").permitAll()
            // admin api restricted to... ADMIN
            .antMatchers("/ws/admin/**").hasRole("ADMIN")
            // and the rest is allowed by any authenticated user
            .antMatchers("/ws/sec/**").authenticated()
            .and()
            .httpBasic();
  }

  private void disableCsrfForNonBrowserApi(HttpSecurity http) throws Exception {
    http.csrf().disable();
  }
}
```

Since [csrf protection](https://en.wikipedia.org/wiki/Cross-site_request_forgery) is useless for REST api, we disable it. See [spring doc](http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#when-to-use-csrf-protection) as well.
Note the ```@Order``` to apply REST security configurer first.

## Secure the MVC part with form login & csrf protection while allowing webjars and h2-console
It is done in the [BrowserSecurityConfig](https://github.com/Orange-OpenSource/spring-security-formlogin-restbasic/blob/master/src/main/java/com/orange/spring/demo/biz/security/BrowserSecurityConfig.java) class:
```java
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
```

So what are we doing here:
 - as we use [webjars](https://spring.io/blog/2014/01/03/utilizing-webjars-in-spring-boot) to retrieve client libraries like jQuery or Bootstrap, we need to disable security to allow the client to access these javascript files!
 - as we use hibernate to store user accounts, we need to disable security in development mode, to access the h2-console. Check this [link]([here](https://springframework.guru/using-the-h2-database-console-in-spring-boot-with-spring-security/) as well
 - then we configure authorized requests, for anonymous, authenticated users, admin...

## Test a secured app... 
The first time you enable security in your application, there is good chance that many integration or unit tests will fail.

There are two points to consider here:
 - you think you have secured the application. You need to write tests to be sure!
 - how to ease business tests when security is annoying you, ie. business tests fail not due to a bug but due to a restricted access problem. 

A few links which can help:
 - [Spring security doc on testing](http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#test)
 - [Preview Spring Security Test: Method Security](https://spring.io/blog/2014/05/07/preview-spring-security-test-method-security)
 - [Testing Improvements in Spring Boot 1.4](https://dzone.com/articles/testing-improvements-in-spring-boot-14?utm_medium=feed&utm_source=feedpress.me&utm_campaign=Feed:%20dzone%2Fjava)


A few points worthy to notice:
 - some annotations let you mock authentication in your tests: ```@WithMockUser @WithAnonymousUser @WithUserDetails```. Check the [doc]([Spring security doc on testing](http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#test)). So if a business test requires to be authenticated, just mock it with ```@WithMockUser``` and you are (almost) done.
 - ```MockMvc``` is just great to test your controllers, see the [doc](http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#test-mockmvc-setup)

Indeed, ```MockMvc``` lets you perform requests with mocked ```csrf``` protection, or with ```http-basic```, etc.
 
For instance to test REST authentication with http basic:
```java
  @Test
  public void retrieveUsersAsAnAuthenticatedUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(MockMvcRequestBuilders.get("/ws/sec/users")
                            .with(httpBasic(username, password))
                            .accept(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk());
  }
```

or to do a request with mocked csrf and authenticated as admin:
```java
  @Test
  @WithMockUser(username = "admin", password = "admin password", roles = "ADMIN")
  public void adminIsAuthorizedToCreateUser() throws Exception {
    // given
    mockMvc
            // do
            .perform(
                    post("/admin/user/create")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "titi")
                            .param("password", "toto")
            )
            // then
            .andExpect(status().isOk());
  }
```

# Domain
We try to follow this architecture:
 - a persistence layer
 - a domain layer, which has no dependency to the persistence layer (so that changes to the persistence layer should not impact the domain).
 - a service layer which is the interface between the domain and the persistence layers
 - a controllers layer which rely on domain objects and services

In addition we try to have immutable objects in the domain:
```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/** let's try to be immutable **/
public class User {
  // as we are immutable, we do not need getters and we can allow direct access to fields
  public final long id;
  public final String username;
}
```

About ```@RequiredArgsConstructor```, it is just a sugar, see [lombok doc](https://projectlombok.org/features): it writes the constructor for us with mandatory (final) fields.

# License, authors
GPLv2, Copyright (C) 2016 Orange

Christophe Maldivi & Denis Boisset

# My side notes

## TODO
- use rolling logs to avoid filling the disk...
- retry to use pre/post auth for repository, see [spring data example here](https://github.com/spring-projects/spring-data-examples/blob/master/rest/security/src/main/java/example/springdata/rest/security/ItemRepository.java). Right now using "@EnableGlobalMethodSecurity(prePostEnabled = true)" involves a "object already built" exception. Maybe due to 1.4.0_M2 version?
- apply the uniq constraint on userDB
- improve UI to let admin remove users
- improve UI to let admin create a user as an administrator

## IntelliJ notes
As we use [lombok](https://projectlombok.org/), you need to add the lombok plugin to IntelliJ

## Hibernate
console is enabled in dev mode (disabled by default)
 - url: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
 - driver: jdbc:h2:mem:testdb

More details [here](https://springframework.guru/using-the-h2-database-console-in-spring-boot-with-spring-security/)

## Hot reloading
### a - Code hot reload
 - run the server on the command line with maven (mvn spring-boot:run)
 - in IntelliJ, make sure that you have selected: Preferences->'Build, Execution, Deployment'->Compiler->'Make project automatically'
 - for thymeleaf templates, it is necessary to force a build to be sure to use the new version

### b - Browser hot reload
Here we need to install the 'livereload' plugin in the browser, see [here](http://livereload.com/extensions/)
