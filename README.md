# access-control-spring-boot-starter

The security was originally based on [Spring Boot Security Application](https://github.com/bkielczewski/example-spring-boot-security) with the following enhancements:

* Change password will be available for all users
* Admin users can reset and deactivate other user accounts
* User lockout after a number of failed login attempts
* Unique CSRF token per request
* Audit logging

The new starter component access-control-spring-boot-starter will have all functionality available by default but can be switched on or off and configured via application.properties

### Getting Started

A [quick guide](https://github.com/tuongl/access-control-spring-boot-starter/wiki/Getting-Started) to get started can be found in our wikis.

### Requirements

* JDK 8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Maven 3.3.9 (https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html#getting-started-maven-installation)

### Quick start

* Clone the repository

```shell
git clone https://github.com/tuongl/access-control-spring-boot-starter.git
```

* Start the application

```shell
cd access-control-spring-boot-starter
mvn spring-boot:run
```

* Point your browser to http://localhost:8080/

### Use in another project

To get the security module into your build

* Add the JitPack repository to your build file
```
  <repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
  </repositories>
```

* Add the dependency
```
    <dependency>
        <groupId>com.github.tuongl</groupId>
        <artifactId>access-control-spring-boot-starter</artifactId>
        <version>v1.0.0</version>
    </dependency>
```
