package gr.hua.dit.project.mycitygov.web.rest;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * UI Controller for managing citizen/employee/admin registration.
 */
@Controller
public class RegistrationController {


    private final UserRepository userRepository;

    public RegistrationController(UserRepository userRepository) {
        if (userRepository == null) throw new IllegalArgumentException(
                "userRepository cannot be null");
        this.userRepository = userRepository;
    }

    /*
     * Serves the registration form (HTML).
     */
    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {
        // TODO if user is authenticated, redirect to tickets
        // Inital data for the form.
        model.addAttribute("user", new User(null, "", "",
                "", "", "", "",
                "", UserType.CITIZEN));
        return "register"; // the name of the thymeleaf/HTML template.
    }


    /*
     * Handles the registration form submission (POST HTTP request).
     */
    @PostMapping("/register")
    public String handleRegistrationFormSubmission(
            @ModelAttribute("user") User user,
            final Model model
    ) {
        System.out.println(user.toString()); // pre-save
        user = this.userRepository.save(user);
        System.out.println(user.toString()); // post-save (we expect a non-null ID)
        model.addAttribute("user", user);
        return "redirect:/register"; // redirect to login form
    }

}
