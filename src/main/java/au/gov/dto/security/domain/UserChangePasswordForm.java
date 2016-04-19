package au.gov.dto.security.domain;

import org.hibernate.validator.constraints.NotEmpty;

public class UserChangePasswordForm {

    private long id;

    private String email = "";

    @NotEmpty
    private String currentPassword = "";

    @NotEmpty
    private String newPassword = "";

    @NotEmpty
    private String newPasswordRepeated = "";

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getCurrentPassword() {

        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {

        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {

        return newPassword;
    }

    public void setNewPassword(String newPassword) {

        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeated() {

        return newPasswordRepeated;
    }

    public void setNewPasswordRepeated(String newPasswordRepeated) {

        this.newPasswordRepeated = newPasswordRepeated;
    }

    @Override
    public String toString() {

        return "UserChangePasswordForm{" + "id=" + id + ", email='"
                + email.replaceFirst("@.+", "@***") + '\'' + ", newPassword=***" + '\''
                + ", newPasswordRepeated=***" + '\'' + '}';
    }

}
