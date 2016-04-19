package au.gov.dto.security.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import au.gov.dto.security.Application;
import au.gov.dto.security.domain.UserChangePasswordForm;
import au.gov.dto.security.domain.UserCreateForm;
import au.gov.dto.security.util.MessageUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest(randomPort = true)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webContext;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).apply(springSecurity()).build();
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testGetUserPage() throws Exception {

        mockMvc.perform(get("/user/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("user-profile"));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testGetUserChangePasswordPage() throws Exception {

        mockMvc.perform(get("/user/1/change-password"))
               .andExpect(status().isOk())
               .andExpect(view().name("user-password"));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleUserChangePasswordFormWithIncorrectPassword() throws Exception {

        mockMvc.perform(post("/user/1/change-password").with(csrf())
                                                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                       .param("id", "1")
                                                       .param("currentPassword", "incorrect")
                                                       .param("newPassword", "password")
                                                       .param("newPasswordRepeated", "password")
                                                       .sessionAttr("userChangePasswordForm",
                                                                    new UserChangePasswordForm()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-password"))
               .andExpect(model().hasErrors())
               .andExpect(model().attribute("error",
                                            equalTo(MessageUtils.getMessage("error.password_change.incorrect"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("id", equalTo(1L))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("email", equalTo("demo@localhost"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("currentPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPasswordRepeated",
                                                        isEmptyOrNullString())));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleUserChangePasswordFormWithNewPasswordSameAsCurrentPassword()
            throws Exception {

        mockMvc.perform(post("/user/1/change-password").with(csrf())
                                                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                       .param("id", "1")
                                                       .param("currentPassword", "demo")
                                                       .param("newPassword", "demo")
                                                       .param("newPasswordRepeated", "demo")
                                                       .sessionAttr("userChangePasswordForm",
                                                                    new UserChangePasswordForm()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-password"))
               .andExpect(model().hasErrors())
               .andExpect(model().attribute("error",
                                            equalTo(MessageUtils.getMessage("error.password_change.not_change"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("id", equalTo(1L))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("email", equalTo("demo@localhost"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("currentPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPasswordRepeated",
                                                        isEmptyOrNullString())));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleUserChangePasswordFormWithNewPasswordsDoNotMatch() throws Exception {

        mockMvc.perform(post("/user/1/change-password").with(csrf())
                                                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                       .param("id", "1")
                                                       .param("currentPassword", "demo")
                                                       .param("newPassword", "password")
                                                       .param("newPasswordRepeated", "repeat")
                                                       .sessionAttr("userChangePasswordForm",
                                                                    new UserChangePasswordForm()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-password"))
               .andExpect(model().hasErrors())
               .andExpect(model().attribute("error",
                                            equalTo(MessageUtils.getMessage("error.password_no_match_new"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("id", equalTo(1L))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("email", equalTo("demo@localhost"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("currentPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPasswordRepeated",
                                                        isEmptyOrNullString())));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleUserChangePasswordFormWithInvalidPasswords() throws Exception {

        mockMvc.perform(post("/user/1/change-password").with(csrf())
                                                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                       .param("id", "1")
                                                       .param("currentPassword", "demo")
                                                       .param("newPassword", "password123")
                                                       .param("newPasswordRepeated", "password123")
                                                       .sessionAttr("userChangePasswordForm",
                                                                    new UserChangePasswordForm()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-password"))
               .andExpect(model().hasErrors())
               .andExpect(model().attribute("error",
                                            equalTo(MessageUtils.getMessage("error.password_invalid"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("id", equalTo(1L))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("email", equalTo("demo@localhost"))))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("currentPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPassword", isEmptyOrNullString())))
               .andExpect(model().attribute("userChangePasswordForm",
                                            hasProperty("newPasswordRepeated",
                                                        isEmptyOrNullString())));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleResetPasswordDeactivate() throws Exception {

        mockMvc.perform(post("/admin/user/create").with(csrf())
                                                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                  .param("email", "user@localhost")
                                                  .param("password", "Password123")
                                                  .param("passwordRepeated", "Password123")
                                                  .param("role", "USER")
                                                  .sessionAttr("userCreateForm",
                                                               new UserCreateForm()))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/admin/user/list"));
        mockMvc.perform(post("/admin/user/2/reset-password").with(csrf()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-list"));
        mockMvc.perform(post("/admin/user/2/deactivate").with(csrf()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-list"));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testGetUserCreatePage() throws Exception {

        mockMvc.perform(get("/admin/user/create"))
               .andExpect(status().isOk())
               .andExpect(view().name("user-add"));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleUserCreateFormWithUnmatchedPasswords() throws Exception {

        mockMvc.perform(post("/admin/user/create").with(csrf())
                                                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                  .param("email", "user@localhost")
                                                  .param("password", "password1")
                                                  .param("passwordRepeated", "password2")
                                                  .param("role", "USER")
                                                  .sessionAttr("userCreateForm",
                                                               new UserCreateForm()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-add"))
               .andExpect(model().hasErrors())
               .andExpect(model().attribute("error",
                                            equalTo(MessageUtils.getMessage("error.password_no_match"))))
               .andExpect(model().attribute("userCreateForm",
                                            hasProperty("email", equalTo("user@localhost"))))
               .andExpect(model().attribute("userCreateForm",
                                            hasProperty("password", equalTo("password1"))))
               .andExpect(model().attribute("userCreateForm",
                                            hasProperty("passwordRepeated", equalTo("password2"))));
    }

    @Test
    @WithUserDetails("demo@localhost")
    public void testHandleUserCreateFormWithInvalidPasswords() throws Exception {

        mockMvc.perform(post("/admin/user/create").with(csrf())
                                                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                  .param("email", "user@localhost")
                                                  .param("password", "password123")
                                                  .param("passwordRepeated", "password123")
                                                  .param("role", "USER")
                                                  .sessionAttr("userCreateForm",
                                                               new UserCreateForm()))
               .andExpect(status().isOk())
               .andExpect(view().name("user-add"))
               .andExpect(model().hasErrors())
               .andExpect(model().attribute("error",
                                            equalTo(MessageUtils.getMessage("error.password_invalid"))))
               .andExpect(model().attribute("userCreateForm",
                                            hasProperty("email", equalTo("user@localhost"))))
               .andExpect(model().attribute("userCreateForm",
                                            hasProperty("password", equalTo("password123"))))
               .andExpect(model().attribute("userCreateForm",
                                            hasProperty("passwordRepeated",
                                                        equalTo("password123"))));
    }
}
