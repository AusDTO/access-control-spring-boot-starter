package au.gov.dto.security.domain.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.gov.dto.security.domain.UserCreateForm;
import au.gov.dto.security.service.UserService;
import au.gov.dto.security.util.MessageUtils;

@Component
public class UserCreateFormValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateFormValidator.class);

    private final UserService userService;

    @Autowired
    public UserCreateFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(UserCreateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        LOGGER.debug("Validating {}", target);
        UserCreateForm form = (UserCreateForm) target;
        validateEmail(errors, form);
        validatePasswords(errors, form);
    }

    private void validateEmail(Errors errors, UserCreateForm form) {

        if (userService.getUserByEmail(form.getEmail()).isPresent()) {
            errors.reject("user_add.email_exists",
                          MessageUtils.getMessage("error.user_add.email_exists"));
        }
    }

    private void validatePasswords(Errors errors, UserCreateForm form) {

        String retVal = BaseRule.validatePassword(form.getPassword(), form.getPasswordRepeated());
        if (!retVal.equals("success")) {
            errors.reject("user_add.password_invalid", retVal);
        }
    }
}
