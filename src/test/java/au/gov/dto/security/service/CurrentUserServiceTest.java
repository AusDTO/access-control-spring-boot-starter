package au.gov.dto.security.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.gov.dto.security.Application;
import au.gov.dto.security.domain.CurrentUser;
import au.gov.dto.security.domain.Role;
import au.gov.dto.security.domain.User;
import au.gov.dto.security.service.CurrentUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CurrentUserServiceTest {

    @Autowired
    private CurrentUserService service;

    @Test
    public void testAdminUserCanAccessSelf() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("demo@localhost");
        user.setPasswordHash("password");
        user.setRole(Role.ADMIN);
        CurrentUser currentUser = new CurrentUser(user);

        assertTrue(service.canAccessUser(currentUser, 1L));
    }

    @Test
    public void testAdminUserCanAccessOther() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("demo@localhost");
        user.setPasswordHash("password");
        user.setRole(Role.ADMIN);
        CurrentUser currentUser = new CurrentUser(user);

        assertTrue(service.canAccessUser(currentUser, 2L));
    }

    @Test
    public void testUserCanAccessSelf() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("demo@localhost");
        user.setPasswordHash("password");
        user.setRole(Role.USER);
        CurrentUser currentUser = new CurrentUser(user);

        assertTrue(service.canAccessUser(currentUser, 1L));
    }

    @Test
    public void testUserCannotAccessOther() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("demo@localhost");
        user.setPasswordHash("password");
        user.setRole(Role.USER);
        CurrentUser currentUser = new CurrentUser(user);

        assertFalse(service.canAccessUser(currentUser, 2L));
    }
}
