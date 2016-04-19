package au.gov.dto.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import au.gov.dto.security.domain.CurrentUser;
import au.gov.dto.security.domain.User;
import au.gov.dto.security.exception.LockedUserException;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserDetailsService.class);
    private static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");

    private final UserService userService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    public CurrentUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {

        LOGGER.info(AUDIT, "Authenticating user with email={}", email.replaceFirst("@.*", "@***"));

        if (loginAttemptService.isBlocked(email)) {
            throw new LockedUserException();
        }

        User user = userService.getUserByEmail(email)
                               .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email=%s was not found",
                                                                                              email)));
        return new CurrentUser(user);
    }

}
