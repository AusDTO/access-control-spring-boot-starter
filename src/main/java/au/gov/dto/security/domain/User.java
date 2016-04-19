package au.gov.dto.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user")
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    private boolean accountLocked;

    public void setId(Long id) {

        this.id = id;
    }

    public Long getId() {

        return id;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPasswordHash() {

        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {

        this.passwordHash = passwordHash;
    }

    public Role getRole() {

        return role;
    }

    public void setRole(Role role) {

        this.role = role;
    }

    public String getUsername() {

        return email.substring(0, email.indexOf('@'));
    }

    public boolean isAccountLocked() {

        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {

        this.accountLocked = accountLocked;
    }

    @Override
    public String toString() {

        return "User{" + "id=" + id + ", email='" + email.replaceFirst("@.*", "@***")
                + "', passwordHash='"
                + passwordHash.substring(0, Math.min(passwordHash.length(), 10)) + "', role=" + role
                + '}';
    }
}
