package gr.hua.dit.project.mycitygov.web.ui;


import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for managing profile.
 */
public class ProfileController {

    @GetMapping("/profile")
    public String showProfile(){
        return "profile";
    }
}
