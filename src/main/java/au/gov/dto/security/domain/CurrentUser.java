package au.gov.dto.security.domain;

import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private User user;

    public CurrentUser(User user) {
        super(user.getUsername(),
              user.getPasswordHash(),
              AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {

        return user;
    }

    public Long getId() {

        return user.getId();
    }

    public Role getRole() {

        return user.getRole();
    }

    public boolean isAdmin() {

        return user.getRole() == Role.ADMIN;
    }

    @Override
    public String toString() {

        return "CurrentUser{" + "user=" + user + "} " + super.toString();
    }
}
