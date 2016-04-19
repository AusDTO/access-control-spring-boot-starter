package au.gov.dto.security.domain;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import au.gov.dto.security.common.Constants;

@MappedSuperclass
public abstract class AuditEntity {

    @NotNull
    private String lastModifiedUser;

    @NotNull
    private Date lastModifiedDate;

    @PrePersist
    @PreUpdate
    public void prePersist() {

        this.lastModifiedUser = getUsernameOfAuthenticatedUser();

        if (lastModifiedUser == null) {
            this.lastModifiedUser = Constants.AUDIT_USER;
        }
        this.lastModifiedDate = new Date();
    }

    private String getUsernameOfAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return ((CurrentUser) authentication.getPrincipal()).getUser().getEmail();
    }

    public String getLastModifiedUser() {

        return lastModifiedUser;
    }

    public void setLastModifiedUser(String lastModifiedUser) {

        this.lastModifiedUser = lastModifiedUser;
    }

    public Date getLastModifiedDate() {

        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {

        this.lastModifiedDate = lastModifiedDate;
    }

}