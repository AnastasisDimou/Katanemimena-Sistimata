package gr.hua.dit.project.mycitygov.web.ui;

import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.service.UserBusinessLogicService;
import gr.hua.dit.project.mycitygov.core.service.model.CreateUserRequest;
import gr.hua.dit.project.mycitygov.core.service.model.CreateUserResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * UI controller for user registration, login and logout.
 */
@Controller
public class AuthController {

    private final UserBusinessLogicService userService;

    public AuthController(final UserBusinessLogicService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(final Authentication authentication, final HttpServletRequest request, final Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
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
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        return "logout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Authentication authentication, final Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/profile";
        }
        // Initial data for the form. Default type is CITIZEN.
        final CreateUserRequest createUserRequest = new CreateUserRequest("", "", "", "", "", "", "");
        model.addAttribute("createUserRequest", createUserRequest);
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            final Authentication authentication,
            @Valid @ModelAttribute("createUserRequest") final CreateUserRequest createUserRequest,
            final BindingResult bindingResult,
            final Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/profile";
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        final CreateUserResult createResult = this.userService.createUser(createUserRequest);

        if (createResult.created()) {
            // FIXED: Redirect directly to profile page upon successful registration
            // Note: The user will still need to log in unless auto-login is implemented in
            // the service
            return "redirect:/profile";
        }

        model.addAttribute("createUserRequest", createUserRequest);
        model.addAttribute("errorMessage", createResult.reason()); // Show service error
        return "register";
    }

    public static boolean isAuhtenticated(final Authentication auth) {
        return auth != null
                || auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken;
    }

    public static boolean isAnonymous(final Authentication auth) {
        return auth == null
                || auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken;
    }
}