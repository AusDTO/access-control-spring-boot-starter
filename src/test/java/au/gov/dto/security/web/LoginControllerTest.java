package au.gov.dto.security.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import au.gov.dto.security.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest(randomPort = true)
public class LoginControllerTest {

    @Autowired
    private EmbeddedWebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                 .apply(springSecurity())
                                 .build();
    }

    // @Test
    public void testAdminLogin() throws Exception {

        mockMvc.perform(formLogin("/login").user("demo@localhost").password("demo"))
               .andExpect(authenticated());
    }

    @Test
    public void testLoginInvalid() throws Exception {

        mockMvc.perform(formLogin("/login").user("demo@localhost").password("invalid"))
               .andExpect(unauthenticated());
    }

    /*
     * @Test public void testRetainInvalidEmail() throws Exception {
     * mockMvc.perform(formLogin("/login").user("demo@localhost").password(
     * "invalid"))
     * .andExpect(model().attributeExists(PermitConstants.PARAM_EMAIL))
     * .andExpect(model().attribute(PermitConstants.PARAM_EMAIL,
     * "demo@localhost")); }
     */

}
