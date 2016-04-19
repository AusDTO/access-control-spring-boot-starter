package au.gov.dto.security.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.gov.dto.security.Application;
import au.gov.dto.security.domain.Role;
import au.gov.dto.security.domain.User;
import au.gov.dto.security.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void testFindUserByEmail() throws Exception {

        Optional<User> user = repository.findOneByEmail("demo@localhost");
        assertTrue(user.isPresent());
        assertEquals("demo@localhost", user.get().getEmail());
        assertEquals(Role.ADMIN, user.get().getRole());

        user = repository.findOneByEmail("nobody@localhost");
        assertFalse(user.isPresent());
    }

}
