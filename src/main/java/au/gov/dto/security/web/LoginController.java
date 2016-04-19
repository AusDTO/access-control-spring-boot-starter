package au.gov.dto.security.web;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.gov.dto.security.common.Constants;

@Controller
public class LoginController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(@RequestParam Optional<String> logout,
                               HttpServletRequest request,
                               Model model) {

        String status = (String) request.getSession().getAttribute("status");
        LOGGER.info(AUDIT, "Getting login page, status={}, logout={}", status, logout);

        if (status != null) {
            model.addAttribute(status, status);
        }

        if (logout.isPresent()) {
            model.addAttribute("logout", logout);
        }

        model.addAttribute(Constants.PARAM_EMAIL,
                           request.getSession().getAttribute(Constants.PARAM_EMAIL) != null
                                   ? request.getSession().getAttribute(Constants.PARAM_EMAIL)
                                   : null);

        return "login";

    }

}
