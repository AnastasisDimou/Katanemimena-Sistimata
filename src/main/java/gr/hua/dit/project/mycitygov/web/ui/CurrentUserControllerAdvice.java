package gr.hua.dit.project.mycitygov.web.ui;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import gr.hua.dit.project.mycitygov.core.security.CurrentUser;
import gr.hua.dit.project.mycitygov.core.security.CurrentUserProvider;

@ControllerAdvice(basePackageClasses = { ProfileController.class, TicketsController.class })
public class CurrentUserControllerAdvice {
   private final CurrentUserProvider currentUserProvider;

   public CurrentUserControllerAdvice(final CurrentUserProvider currentUserProvider) {
      if (currentUserProvider == null) {
         throw new IllegalArgumentException("currentUserProvider cannot be null");
      }
      this.currentUserProvider = currentUserProvider;
   }

   @ModelAttribute("me")
   public CurrentUser addCurrentUserToModel() {
      return this.currentUserProvider.getCurrentUser().orElse(null);
   }
}
