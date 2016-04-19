package au.gov.dto.security.domain.validator;

interface PasswordRule {

    boolean passRule(String password);

    String failMessage();
}