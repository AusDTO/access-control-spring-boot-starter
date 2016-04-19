package au.gov.dto.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.gov.dto.security.Application;
import au.gov.dto.security.domain.Role;
import au.gov.dto.security.domain.User;
import au.gov.dto.security.domain.UserChangePasswordForm;
import au.gov.dto.security.domain.UserCreateForm;
import au.gov.dto.security.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@WithUserDetails("demo@localhost")
@SpringApplicationConfiguration(classes = Application.class)
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    public void testGetUserById() throws Exception {

        Optional<User> user = service.getUserById(1L);
        assertEquals(Long.valueOf(1), user.get().getId());
        assertEquals("demo@localhost", user.get().getEmail());
        assertEquals(Role.ADMIN, user.get().getRole());
    }

    @Test
    public void testGetUserByEmail() throws Exception {

        Optional<User> user = service.getUserByEmail("demo@localhost");
        assertEquals(Long.valueOf(1), user.get().getId());
        assertEquals("demo@localhost", user.get().getEmail());
        assertEquals(Role.ADMIN, user.get().getRole());
    }

    @Test
    public void testGetAllUsers() throws Exception {

        List<User> users = new ArrayList<User>(service.getAllUsers());
        assertEquals(1, users.size());
        assertEquals("demo@localhost", users.get(0).getEmail());
    }

    @Test
    @Transactional
    public void testCreateDeactivateUser() throws Exception {

        UserCreateForm form = new UserCreateForm();
        form.setEmail("newuser@localhost");
        form.setPassword("password");
        form.setPasswordRepeated("password");
        form.setRole(Role.USER);
        User user = service.create(form);
        assertNotNull(user);

        service.deactivate(user.getId());
        assertTrue(service.getAllUsers().size() == 1);
    }

    @Test
    @Transactional
    public void testChangePassword() throws Exception {

        UserChangePasswordForm form = new UserChangePasswordForm();
        form.setId(1);
        form.setEmail("demo@localhost");
        form.setCurrentPassword("password");
        form.setNewPassword("newPassword");
        form.setNewPasswordRepeated("newPassword");
        User user = service.changePassword(form);
        assertNotNull(user);
    }

    @Test
    @Transactional
    public void testResetPassword() throws Exception {

        String temp = service.resetPassword(1);
        assertTrue(!StringUtils.isEmpty(temp));
    }
}
