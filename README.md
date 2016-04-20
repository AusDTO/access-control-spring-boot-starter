# access-control-spring-boot-starter
This guide walks you through the process of creating a simple web application with resources that are protected by Spring Security.

### Getting Started

This section will guide you through the setup and basic usage of Security Starter component.

#### Create an unsecured web application

Before you can apply security to a web application, you need a web application to secure. The steps in this section walk you through creating a very simple web application. Then you secure it with Spring Security in the next section.

The web application includes a simple view: home page. The home page is defined in the following Thymeleaf template:

`src/main/resources/templates/home.html`

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
          xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
        <head>
            <title>Hello World!</title>
        </head>
        <body>
            <h1>Hello world!</h1>
        </body>
    </html>

The web application is based on Spring MVC. Thus you need to configure Spring MVC and set up view controllers to expose these templates. Here’s a configuration class for configuring Spring MVC in the application.

`src/main/java/au/gov/dto/security/MvcConfig.java`

    @Configuration
    public class MvcConfig extends WebMvcConfigurerAdapter {
    
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
    
            registry.addViewController("/home").setViewName("home");
            registry.addViewController("/").setViewName("home");
            registry.addViewController("/login").setViewName("login");
        }
    
    }

The `addViewControllers()` method (overriding the method of the same name in `WebMvcConfigurerAdapter`) adds three view controllers. Two of the view controllers reference the view whose name is "home" (defined in home.html). The fourth view controller references another view named "login". You’ll create that view in the next section.

At this point, you could jump ahead to make the application executable and run the application without having to login to anything.

With the base simple web application created, you can add security to it.

#### Set up Spring Security

If you’re a Maven user just add the Access Control Starter library as a dependency:

    <dependency>
      <groupId>au.gov.dto.dibp</groupId>
      <artifactId>access-control-spring-boot-starter</artifactId>
      <version>1.0.0</version>
    </dependency>

Alternatively, clone the Git repository to get access to the source and incorporate it directly into your project.

The following properties can be added to your `application.properties` to tailor the configuration to suit your need.

| Property                         | Value      | Default  | Description                       |
|----------------------------------|------------|----------|-----------------------------------|
| security.basic.enabled           | true/false | false    | Turn Basic Authentication on      |
| security.enable-csrf             | true/false | true     | Enable CSRF protection            |
| access.control.max-login-attempt | int        | 5        | Max login attempts before lockout |

The spring.messages.basename property (default to `messages`) will need to be added to include `security-messages` to get access to messages defined in `security-messages.properties` file.

At this stage, we need to modify the home.html to ask the users to "Sign in" and contain a "Sign out" form as shown below

`src/main/resources/templates/home.html`

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
          xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
      <title>Hello World!</title>
      <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}"
      	href="../../css/bootstrap.min.css" />
    </head>
    <body>
    	<div class="container">
    		<div class="navbar">
    			<div class="navbar-inner">
    				<a class="brand" href="http://www.thymeleaf.org"> Thymeleaf -	Plain </a>
    				<ul class="nav">
              <li th:if="${currentUser eq null}"><a href="/login">Sign in</a></li>
    					<li th:if="${currentUser}"><a th:href="@{/}" href="home.html"> Home </a></li>
    					<li th:if="${currentUser}"><a href="#" onclick="document.getElementById('logoutForm').submit();"> Sign out </a></li>
    				</ul>
    			</div>
    		</div>
    		<h1>Hello world!</h1>
        <div>
          <form name="form" id="logoutForm" action="/logout" method="POST" style="display: none !important; visibility: hidden;">
            <input type="submit" id="logout" value="logout" />
            <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
          </form>
         </div>
    	</div>
    </body>
    </html>

When a user successfully logs in, they will be redirected to the previously requested page that requires authentication, ie. home page. There is a custom "/login" page and everyone is allowed to view it.

Out of the box the Access Control Starter library sets up a user store in database with a single user. That user is given an email of "demo@localhost", a password of "demo", and a role of "USER".

Now we need to create the login page. There’s already a view controller for the "login" view, so you only need to create the login view itself.

`src/main/resources/templates/login.html`

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
          xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
      <title>Login</title>
      <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}" href="../../css/bootstrap.min.css" />
    </head>
    <body>
    	<div class="container">
    		<div class="navbar">
    			<div class="navbar-inner">
    				<a class="brand" href="http://www.thymeleaf.org"> Thymeleaf -	Plain </a>
    				<ul class="nav">
    					<li><a th:href="@{/}" href="home.html"> Home </a></li>
    				</ul>
    			</div>
    		</div>
    		<div class="content">
    			<p th:if="${param.logout}" class="alert">You have been logged out</p>
    			<p th:if="${param.error}" class="alert alert-error">There was an error, please try again</p>
    			<h2>Login with Email and Password</h2>
    			<form name="form" action="/login" method="POST">
    				<fieldset>
    					<input type="text" name="email" value="" placeholder="Email" required="required" autofocus="autofocus" />
    					<input type="password" name="password" placeholder="Password" required="required" />
    				</fieldset>
    				<input type="submit" id="login" value="Login"	class="btn btn-primary" />
            <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    			</form>
    		</div>
    	</div>
    </body>
    </html>

As you can see, this Thymeleaf template simply presents a form that captures a username and password and posts them to "/login". As configured, Spring Security provides a filter that intercepts that request and authenticates the user. If the user fails to authenticate, the page is redirected to "/login?error" and our page displays the appropriate error message. Upon successfully signing out, our application is sent to "/login?logout" and our page displays the appropriate success message.

Last we need to provide the user a way to display the current username and Sign Out. Update the home.html to say hello to the current user as shown below

`src/main/resources/templates/home.html`

    <h1 th:if="${currentUser eq null}">Hello World!</h1>
    <h1 th:if="${currentUser}" th:inline="text">Hello [[${#httpServletRequest.remoteUser}]]!</h1>

We display the username by using Spring Security’s integration with HttpServletRequest#getRemoteUser().

If you click on the "Sign out" link, your authentication is revoked, and you are returned to the log in page with a message indicating you are logged out.

#### Summary

Congratulations! You have developed a simple web application that is secured with Spring Security.
