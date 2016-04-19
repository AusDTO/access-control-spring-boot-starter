package au.gov.dto.security.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "access.control")
public class SecuritySettings {

    private int maxLoginAttempt;

    private String selfRegistration;

    private String[] roles;

    public int getMaxLoginAttempt() {

        return maxLoginAttempt;
    }

    public void setMaxLoginAttempt(int maxLoginAttempt) {

        this.maxLoginAttempt = maxLoginAttempt;
    }

    public String getSelfRegistration() {

        return selfRegistration;
    }

    public void setSelfRegistration(String selfRegistration) {

        this.selfRegistration = selfRegistration;
    }

    public String[] getRoles() {

        return roles;
    }

    public void setRoles(String[] roles) {

        this.roles = roles;
    }

}
