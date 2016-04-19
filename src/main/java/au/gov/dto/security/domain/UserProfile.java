package au.gov.dto.security.domain;

public class UserProfile {

    private User user;
    private boolean self;

    public UserProfile(User user, long id) {
        this.user = user;
        self = (user.getId() == id);
    }

    public User getUser() {

        return user;
    }

    public Long getId() {

        return user.getId();
    }

    public String getEmail() {

        return user.getEmail();
    }

    public Role getRole() {

        return user.getRole();
    }

    public boolean isSelf() {

        return self;
    }

    @Override
    public String toString() {

        return "UserProfile{" + "user=" + user + "} " + super.toString();
    }
}
