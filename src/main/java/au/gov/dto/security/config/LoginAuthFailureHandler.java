package au.gov.dto.security.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import au.gov.dto.security.common.Constants;
import au.gov.dto.security.service.LoginAttemptService;

@Component
public class LoginAuthFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        if (exception != null) {

            if (request.getParameter(Constants.PARAM_EMAIL) != null) {
                loginAttemptService.loginFailed(request.getParameter(Constants.PARAM_EMAIL));

                // To retain invalid email address
                request.getSession().setAttribute(Constants.PARAM_EMAIL,
                                                  request.getParameter(Constants.PARAM_EMAIL));

                if (loginAttemptService.isBlocked(request.getParameter(Constants.PARAM_EMAIL))) {
                    request.getSession().setAttribute("status", "locked");
                    // using request parameters as part of the url is not
                    // working as expected
                    // response.sendRedirect("/login?locked");
                    response.sendRedirect("/login");
                } else {
                    request.getSession().setAttribute("status", "error");
                    response.sendRedirect("/login");
                }
            }
        }
    }

}
