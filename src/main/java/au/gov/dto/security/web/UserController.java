package au.gov.dto.security.web;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import au.gov.dto.security.common.Constants;
import au.gov.dto.security.domain.CurrentUser;
import au.gov.dto.security.domain.User;
import au.gov.dto.security.domain.UserChangePasswordForm;
import au.gov.dto.security.domain.UserCreateForm;
import au.gov.dto.security.domain.UserProfile;
import au.gov.dto.security.domain.validator.UserChangePasswordFormValidator;
import au.gov.dto.security.domain.validator.UserCreateFormValidator;
import au.gov.dto.security.service.LoginAttemptService;
import au.gov.dto.security.service.UserService;
import au.gov.dto.security.util.MessageUtils;

@Controller
public class UserController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserCreateFormValidator userCreateFormValidator;
    private final UserChangePasswordFormValidator userChangePasswordFormValidator;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    public UserController(UserService userService, UserCreateFormValidator userCreateFormValidator,
            UserChangePasswordFormValidator userChangePasswordFormValidator) {
        this.userService = userService;
        this.userCreateFormValidator = userCreateFormValidator;
        this.userChangePasswordFormValidator = userChangePasswordFormValidator;
    }

    @InitBinder("userCreateForm")
    public void initUserCreateFormBinder(WebDataBinder binder) {

        binder.addValidators(userCreateFormValidator);
    }

    @InitBinder("userChangePasswordForm")
    public void initUserChangePasswordFormBinder(WebDataBinder binder) {

        binder.addValidators(userChangePasswordFormValidator);
    }

    @PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
    @RequestMapping("/user/{id}")
    public ModelAndView getUserPage(@PathVariable Long id) {

        LOGGER.info(AUDIT, "Getting user page for user={}", id);

        User user = userService.getUserById(id)
                               .orElseThrow(() -> new NoSuchElementException(String.format("User=%s not found",
                                                                                           id)));

        user.setAccountLocked(loginAttemptService.isBlocked(user.getEmail()));

        UserProfile profile = new UserProfile(user,
                                              ((CurrentUser) SecurityContextHolder.getContext()
                                                                                  .getAuthentication()
                                                                                  .getPrincipal()).getId());
        return new ModelAndView("user-profile", "user", profile);
    }

    @PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
    @RequestMapping(value = "/user/{id}/change-password", method = RequestMethod.GET)
    public ModelAndView getUserChangePasswordPage(@PathVariable Long id) {

        LOGGER.debug("Getting user change password page for user={}", id);

        Optional<User> user = userService.getUserById(id);
        UserChangePasswordForm form = new UserChangePasswordForm();
        form.setId(user.get().getId());
        form.setEmail(user.get().getEmail());

        return new ModelAndView("user-password", "userChangePasswordForm", form);
    }

    @PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
    @RequestMapping(value = "/user/{id}/change-password", method = RequestMethod.POST)
    public ModelAndView handleUserChangePasswordForm(@PathVariable Long id,
                                                     @Valid @ModelAttribute("userChangePasswordForm") UserChangePasswordForm form,
                                                     BindingResult bindingResult,
                                                     Model model) {

        LOGGER.info(AUDIT,
                    "Processing user change password form={}, bindingResult={}",
                    form,
                    bindingResult);

        if (bindingResult.hasErrors()) {
            // failed validation
            model.addAttribute("error", bindingResult.getGlobalError().getDefaultMessage());
            Optional<User> user = userService.getUserById(form.getId());
            form.setEmail(user.get().getEmail());
            form.setCurrentPassword(null);
            form.setNewPassword(null);
            form.setNewPasswordRepeated(null);
            return new ModelAndView("user-password", "userChangePasswordForm", form);
        }
        try {
            userService.changePassword(form);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception occurred when trying to change user password", e);
            bindingResult.reject("password.error", "Change password failure");

            Optional<User> user = userService.getUserById(form.getId());
            form.setEmail(user.get().getEmail());
            form.setCurrentPassword(null);
            form.setNewPassword(null);
            form.setNewPasswordRepeated(null);
            return new ModelAndView("user-password", "userChangePasswordForm", form);
        }

        model.addAttribute("success", MessageUtils.getMessage("info.password.changed"));
        User user = userService.getUserById(id)
                               .orElseThrow(() -> new NoSuchElementException(String.format("User=%s not found",
                                                                                           id)));
        UserProfile profile = new UserProfile(user,
                                              ((CurrentUser) SecurityContextHolder.getContext()
                                                                                  .getAuthentication()
                                                                                  .getPrincipal()).getId());
        return new ModelAndView("user-profile", "user", profile);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/user/{id}/reset-password", method = RequestMethod.POST)
    public ModelAndView handleUserResetPassword(@PathVariable Long id, Model model) {

        Optional<User> user = userService.getUserById(id);
        LOGGER.info(AUDIT, "Resetting password for user={}", user.get().getEmail());
        String password = userService.resetPassword(id);
        model.addAttribute("success",
                           MessageUtils.getMessage("info.password.reset",
                                                   new Object[] { user.get().getEmail(),
                                                           password }));
        return new ModelAndView("user-list", "users", userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/user/{id}/deactivate", method = RequestMethod.POST)
    public ModelAndView handleUserDeactivation(@PathVariable Long id, Model model) {

        Optional<User> user = userService.getUserById(id);
        LOGGER.info(AUDIT, "Deactivating user={}", user.get().getEmail());

        userService.deactivate(id);
        model.addAttribute("success",
                           MessageUtils.getMessage("info.user.deactivated",
                                                   new Object[] { user.get().getEmail() }));
        return new ModelAndView("user-list", "users", userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/user/create", method = RequestMethod.GET)
    public ModelAndView getUserCreatePage() {

        LOGGER.debug("Getting user create form");

        return new ModelAndView("user-add", "userCreateForm", new UserCreateForm());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String handleUserCreateForm(@Valid @ModelAttribute("userCreateForm") UserCreateForm form,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        LOGGER.info(AUDIT, "Processing user create form={}, bindingResult={}", form, bindingResult);

        if (bindingResult.hasErrors()) {
            // failed validation
            model.addAttribute("error", bindingResult.getGlobalError().getDefaultMessage());
            return "user-add";
        }
        try {
            userService.create(form);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception occurred when trying to save the user, assuming duplicate email",
                        e);
            bindingResult.reject("email.exists", "Email already exists");
            return "user-add";
        }

        redirectAttributes.addFlashAttribute("success",
                                             MessageUtils.getMessage("info.user.created",
                                                                     new Object[] {
                                                                             form.getEmail() }));
        return "redirect:/admin/user/list";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = { "/admin/user/{email}/unlock" }, method = RequestMethod.POST)
    public String unlockUser(@PathVariable String email,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        LOGGER.info(AUDIT, "Unlock user account {}", email);

        if (!StringUtils.isEmpty(email)) {
            if (loginAttemptService.isBlocked(email)) {
                loginAttemptService.unlock(email);
                redirectAttributes.addFlashAttribute(Constants.SUCCESS,
                                                     MessageUtils.getMessage("success.unlock",
                                                                             new Object[] {
                                                                                     email }));
            }
        }

        return "redirect:/admin/user/list";
    }

}
