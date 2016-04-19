package au.gov.dto.security.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import au.gov.dto.security.domain.CurrentUser;
import au.gov.dto.security.service.LoginAttemptService;

@Component
public class LoginAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        // reset the login attempts count for the current email address if it
        // already exists
        loginAttemptService.unlock(((CurrentUser) authentication.getPrincipal()).getUser()
                                                                                .getEmail());
        response.sendRedirect("/");

    }

}