package gr.hua.dit.project.mycitygov.web.ui;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.port.exception.GovAuthException;
import gr.hua.dit.project.mycitygov.core.security.ApplicationUserDetails;
import gr.hua.dit.project.mycitygov.core.service.GovLoginService;
import gr.hua.dit.project.mycitygov.core.service.model.GovLoginOutcome;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GovLoginController {

   private final GovLoginService govLoginService;

   public GovLoginController(final GovLoginService govLoginService) {
      if (govLoginService == null)
         throw new NullPointerException();
      this.govLoginService = govLoginService;
   }

   @GetMapping("/gov-login")
   public String govLogin(final Authentication authentication) {
      if (AuthUtils.isAuthenticated(authentication)) {
         return "redirect:/profile";
      }
      return "gov-login";
   }

   @PostMapping("/auth/gov/login")
   public String handleGovLogin(
         @RequestParam("afm") final String afm,
         @RequestParam("pin") final String pin,
         final Authentication authentication,
         final HttpServletRequest request) {
      if (AuthUtils.isAuthenticated(authentication)) {
         return "redirect:/profile";
      }

      try {
         final GovLoginOutcome outcome = this.govLoginService.loginWithGov(afm, pin);
         this.authenticateUser(outcome.user(), request);
         return "redirect:/profile";
      } catch (GovAuthException ex) {
         if (ex.reason() == GovAuthException.Reason.INVALID_CREDENTIALS) {
            return "redirect:/gov-login?govError";
         }
         return "redirect:/gov-login?govUnavailable";
      }
   }

   private void authenticateUser(final User user, final HttpServletRequest request) {
      final ApplicationUserDetails principal = new ApplicationUserDetails(
            user.getId(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getUserType());

      final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            principal, null, principal.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      final SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);

      SecurityContextHolder.setContext(context);
      request.getSession(true)
            .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
   }
}
