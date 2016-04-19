package au.gov.dto.security.config;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

public class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {

    protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
    protected static final String REQUEST_HEADER_NAME = "X-CSRF-HEADER";

    protected static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
    protected static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
    protected static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    javax.servlet.FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken token = new DefaultCsrfToken(REQUEST_HEADER_NAME,
                                               REQUEST_ATTRIBUTE_NAME,
                                               UUID.randomUUID().toString());
        HttpSession session = request.getSession(false);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (session == null || authentication == null || !authentication.isAuthenticated()) {
            token = (CsrfToken) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
        } else {
            session.setAttribute(HttpSessionCsrfTokenRepository.class.getName()
                                                                     .concat(".CSRF_TOKEN"),
                                 token);
            request.setAttribute(REQUEST_ATTRIBUTE_NAME, token);
        }

        if (token != null) {
            response.setHeader(RESPONSE_HEADER_NAME, token.getHeaderName());
            response.setHeader(RESPONSE_PARAM_NAME, token.getParameterName());
            response.setHeader(RESPONSE_TOKEN_NAME, token.getToken());
        }

        filterChain.doFilter(request, response);
    }
}