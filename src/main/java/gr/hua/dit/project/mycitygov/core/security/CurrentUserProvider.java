package gr.hua.dit.project.mycitygov.core.security;

import org.springframework.stereotype.Component;

import java.util.Optional;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public final class CurrentUserProvider {

   private final UserRepository userRepository;

   public CurrentUserProvider(final UserRepository userRepository) {
      if (userRepository == null)
         throw new NullPointerException();
      this.userRepository = userRepository;
   }

   public Optional<CurrentUser> getCurrentUser() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null)
         return Optional.empty();

      if (!(authentication.getPrincipal() instanceof ApplicationUserDetails userDetails)) {
         return Optional.empty();
      }

      final User user = this.userRepository.findById(userDetails.userId()).orElse(null);
      if (user == null)
         return Optional.empty();

      return Optional.of(new CurrentUser(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getUserType()));
   }

   public CurrentUser requireCurrentUser() {
      return this.getCurrentUser().orElseThrow(() -> new SecurityException("not authenticated"));
   }
}
