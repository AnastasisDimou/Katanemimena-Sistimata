package gr.hua.dit.project.mycitygov.web.ui;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

   private final UserService userService;

   public TestController(UserService userService) {
      this.userService = userService;
   }

   @GetMapping
   public List<User> getAllUsers() {
      return userService.getAllUsers();
   }

}
