# access-control-spring-boot-starter
This guide walks you through the process of creating a simple web application with resources that are protected by Spring Security.

### Getting Started

This section will guide you through the setup and basic usage of Security Starter component.

#### Setting Up

If you’re a Maven user just add the Access Control Starter library as a dependency:

    <dependency>
      <groupId>au.gov.dto.dibp</groupId>
      <artifactId>access-control-spring-boot-starter</artifactId>
      <version>1.0.0</version>
    </dependency>

Alternatively, clone the Git repository to get access to the source and incorporate it directly into your project.

#### Configuration

The following properties can be added to your `application.properties` to tailor the configuration to suit your need.

| Property                         | Value      | Default  | Description                       |
|----------------------------------|------------|----------|-----------------------------------|
| security.basic.enabled           | true/false | false    | Turn Basic Authentication on      |
| security.enable-csrf             | true/false | true     | Enable CSRF protection            |
| access.control.max-login-attempt | int        | 5        | Max login attempts before lockout |

The spring.messages.basename property (default to `messages`) will need to be added to include `security-messages` to get access to messages defined in `security-messages.properties` file.
