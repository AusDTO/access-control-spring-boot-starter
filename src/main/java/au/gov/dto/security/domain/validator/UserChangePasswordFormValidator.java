package au.gov.dto.security.domain.validator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.gov.dto.security.domain.User;
import au.gov.dto.security.domain.UserChangePasswordForm;
import au.gov.dto.security.service.UserService;
import au.gov.dto.security.util.MessageUtils;

@Component
public class UserChangePasswordFormValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserChangePasswordFormValidator.class);

    private final UserService userService;

    @Autowired
    public UserChangePasswordFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(UserChangePasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        LOGGER.debug("Validating {}", target);
        UserChangePasswordForm form = (UserChangePasswordForm) target;
        validateCurrentPassword(errors, form);
        validatePasswords(errors, form);
    }

    private void validateCurrentPassword(Errors errors, UserChangePasswordForm form) {

        Optional<User> user = userService.getUserById(form.getId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(form.getCurrentPassword(), user.get().getPasswordHash())) {
            errors.reject("password_change.incorrect",
                          MessageUtils.getMessage("error.password_change.incorrect"));
        }
    }

    private void validatePasswords(Errors errors, UserChangePasswordForm form) {

        if (form.getNewPassword().equals(form.getCurrentPassword())) {
            errors.reject("password_change.not_change",
                          MessageUtils.getMessage("error.password_change.not_change"));
        }

        String retVal = BaseRule.validateNewPassword(form.getNewPassword(),
                                                     form.getNewPasswordRepeated());
        if (!retVal.equals("success")) {
            errors.reject("password_change.password_invalid", retVal);
        }
    }
}
