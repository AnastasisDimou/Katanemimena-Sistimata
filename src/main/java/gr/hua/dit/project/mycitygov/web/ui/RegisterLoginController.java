package gr.hua.dit.project.mycitygov.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterLoginController {
   @GetMapping("/login")
   public String showLoginPage() {
      return "login";
   }
}
