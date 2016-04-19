package au.gov.dto.security.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import au.gov.dto.security.service.UserService;

@Controller
public class UsersController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/admin/user/list")
    public ModelAndView getUsersPage() {

        LOGGER.debug("Getting users page");

        return new ModelAndView("user-list", "users", userService.getAllUsers());
    }

}
