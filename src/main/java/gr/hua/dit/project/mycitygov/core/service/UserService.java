package gr.hua.dit.project.mycitygov.core.service;

import gr.hua.dit.project.mycitygov.core.port.repository.UserRepository;
import gr.hua.dit.project.mycitygov.core.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
   private final UserRepository userRepository;

   public UserService(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   public List<User> getAllUsers() {
      return userRepository.findAll();
   }

}
