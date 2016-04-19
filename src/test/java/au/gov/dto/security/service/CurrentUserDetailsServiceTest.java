package au.gov.dto.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import au.gov.dto.security.Application;
import au.gov.dto.security.domain.CurrentUser;
import au.gov.dto.security.domain.Role;
import au.gov.dto.security.service.CurrentUserDetailsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CurrentUserDetailsServiceTest {

    @Autowired
    private CurrentUserDetailsService service;

    @Test
    @Transactional
    public void testLoadUserByUsername() throws Exception {

        CurrentUser user = service.loadUserByUsername("demo@localhost");
        assertNotNull(user);
        assertEquals("demo", user.getUsername());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByInvalidUsername() throws Exception {

        service.loadUserByUsername("nobody@localhost");
    }
}
