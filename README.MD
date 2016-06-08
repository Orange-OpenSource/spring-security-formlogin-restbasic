# Spring security demo app
 - spring security with form login AND secured REST api with http basic auth
 - in memory authent for admin user AND jpa/hibernate persistence for users
 - thymeleaf templates for the ui
 - tests: security is tested for all html pages and the REST api

# Build
```./mvnw clean install```

# Run
```./mvnw spring-boot:run``` and then open your browser at [http://localhost:8080](http://localhost:8080)

## TODO
- use rolling logs to avoid filling the disk...
- retry to use pre/post auth for repository, see [spring data example here](https://github.com/spring-projects/spring-data-examples/blob/master/rest/security/src/main/java/example/springdata/rest/security/ItemRepository.java). Right now using "@EnableGlobalMethodSecurity(prePostEnabled = true)" involves a "object already built" exception. Maybe due to 1.4.0_M2 version?
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

## License
GPLv2

## Author
Orange, Christophe Maldivi
