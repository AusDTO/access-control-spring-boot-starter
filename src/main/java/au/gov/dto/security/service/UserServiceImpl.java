package au.gov.dto.security.service;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import au.gov.dto.security.domain.User;
import au.gov.dto.security.domain.UserChangePasswordForm;
import au.gov.dto.security.domain.UserCreateForm;
import au.gov.dto.security.repository.UserRepository;
import au.gov.dto.security.util.PasswordUtils;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserById(long id) {

        LOGGER.debug("Getting user={}", id);
        return Optional.ofNullable(userRepository.findOne(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {

        LOGGER.debug("Getting user by email={}", email.replaceFirst("@.*", "@***"));
        return userRepository.findOneByEmail(email);
    }

    @Override
    public Collection<User> getAllUsers() {

        LOGGER.debug("Getting all users");
        return userRepository.findAll(new Sort("email"));
    }

    @Override
    public User create(UserCreateForm form) {

        User user = new User();
        user.setEmail(form.getEmail());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(form.getPassword()));
        user.setRole(form.getRole());
        return userRepository.save(user);
    }

    @Override
    public User changePassword(UserChangePasswordForm form) {

        Optional<User> user = Optional.ofNullable(userRepository.findOne(form.getId()));
        user.get().setPasswordHash(new BCryptPasswordEncoder().encode(form.getNewPassword()));
        return userRepository.save(user.get());
    }

    @Override
    public String resetPassword(long id) {

        Optional<User> user = Optional.ofNullable(userRepository.findOne(id));
        String temp = PasswordUtils.generateRandomPassword();
        user.get().setPasswordHash(new BCryptPasswordEncoder().encode(temp));
        userRepository.save(user.get());
        return temp;
    }

    @Override
    public void deactivate(long id) {

        userRepository.delete(id);
    }

}
