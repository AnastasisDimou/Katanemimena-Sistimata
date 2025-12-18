package gr.hua.dit.project.mycitygov.web.ui;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for user registration, login and logout.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
            final Authentication authentication,
            final HttpServletRequest request,
            final Model model) {

        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }
        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Λάθος email ή κωδικός.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "Έχετε αποσυνδεθεί επιτυχώς.");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(final Authentication authentication) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }
        return "logout";
    }

}