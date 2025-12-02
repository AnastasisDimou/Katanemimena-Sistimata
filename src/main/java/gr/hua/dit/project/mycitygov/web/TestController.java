package gr.hua.dit.project.mycitygov.web;

import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class TestController {


    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() {
        return "test";
    }
}
