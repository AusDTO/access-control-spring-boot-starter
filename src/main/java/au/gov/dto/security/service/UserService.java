package au.gov.dto.security.service;

import java.util.Collection;
import java.util.Optional;

import au.gov.dto.security.domain.User;
import au.gov.dto.security.domain.UserChangePasswordForm;
import au.gov.dto.security.domain.UserCreateForm;

public interface UserService {

    Optional<User> getUserById(long id);

    Optional<User> getUserByEmail(String email);

    Collection<User> getAllUsers();

    User create(UserCreateForm form);

    User changePassword(UserChangePasswordForm form);

    String resetPassword(long id);

    void deactivate(long id);
}
