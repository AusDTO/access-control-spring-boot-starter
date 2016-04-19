package au.gov.dto.security.domain.validator;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.dto.security.util.MessageUtils;

public abstract class BaseRule implements PasswordRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRule.class);

    private final String message;

    BaseRule(String message) {
        this.message = message;
    }

    @Override
    public String failMessage() {

        return message;
    }

    public static String validatePassword(String password1, String password2) {

        if (password1 == null || password2 == null) {
            LOGGER.debug("Passwords = null");
            return "One or both passwords are null";
        }

        if (password1.isEmpty() || password2.isEmpty()) {
            LOGGER.debug("Passwords = empty");
            return "One or both passwords are empty";
        }

        if (!password1.equals(password2)) {
            LOGGER.debug("Passwords don't match");
            return MessageUtils.getMessage("error.password_no_match");
        }

        if (password1.length() < 10) {
            LOGGER.debug("Password too short");
            return MessageUtils.getMessage("error.password_too_short");
        }

        if (password1.length() >= 13) {
            return "success";
        }

        String retVal = "";
        int fail = 0;
        for (PasswordRule rule : RULES) {
            if (!rule.passRule(password1)) {
                LOGGER.debug(fail + "<--- " + rule.failMessage());
                retVal = MessageUtils.getMessage("error.password_invalid");
                fail++;
            }
        }

        return fail <= 1 ? "success" : retVal;
    }

    public static String validateNewPassword(String password1, String password2) {

        if (password1 == null || password2 == null) {
            LOGGER.debug("Passwords = null");
            return "One or both passwords are null";
        }

        if (password1.isEmpty() || password2.isEmpty()) {
            LOGGER.debug("Passwords = empty");
            return "One or both passwords are empty";
        }

        if (!password1.equals(password2)) {
            LOGGER.debug("Passwords don't match");
            return MessageUtils.getMessage("error.password_no_match_new");
        }

        if (password1.length() < 10) {
            LOGGER.debug("Password too short");
            return MessageUtils.getMessage("error.password_too_short_new");
        }

        if (password1.length() >= 13) {
            return "success";
        }

        String retVal = "";
        int fail = 0;
        for (PasswordRule rule : RULES) {
            if (!rule.passRule(password1)) {
                LOGGER.debug(fail + "<--- " + rule.failMessage());
                retVal = MessageUtils.getMessage("error.password_invalid");
                fail++;
            }
        }

        return fail <= 1 ? "success" : retVal;
    }

    private static final PasswordRule[] RULES = { new BaseRule("Password needs an upper case") {

        private final Pattern hasUppercase = Pattern.compile("[A-Z]");

        @Override
        public boolean passRule(String password) {

            return hasUppercase.matcher(password).find();
        }
    }, new BaseRule("Password needs a lower case") {

        private final Pattern hasLowercase = Pattern.compile("[a-z]");

        @Override
        public boolean passRule(String password) {

            return hasLowercase.matcher(password).find();
        }
    }, new BaseRule("Password needs a number") {

        private final Pattern hasNumber = Pattern.compile("\\d");

        @Override
        public boolean passRule(String password) {

            return hasNumber.matcher(password).find();
        }
    }, new BaseRule("Password needs a special character i.e. !,@,#, etc.") {

        private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");

        @Override
        public boolean passRule(String password) {

            return hasSpecialChar.matcher(password).find();
        }
    } };
}