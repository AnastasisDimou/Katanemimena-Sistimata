package gr.hua.dit.project.mycitygov.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import gr.hua.dit.project.mycitygov.core.security.ApplicationUserDetails;
import gr.hua.dit.project.mycitygov.core.security.JwtService;
import gr.hua.dit.project.mycitygov.web.api.model.AuthRequest;
import gr.hua.dit.project.mycitygov.web.api.model.AuthResponse;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auth", description = "Token issuance for API clients")
public class AuthResource {

   private final AuthenticationManager authenticationManager;
   private final JwtService jwtService;

   public AuthResource(final AuthenticationManager authenticationManager, final JwtService jwtService) {
      if (authenticationManager == null)
         throw new NullPointerException();
      if (jwtService == null)
         throw new NullPointerException();
      this.authenticationManager = authenticationManager;
      this.jwtService = jwtService;
   }

   @Operation(
         summary = "Issue JWT",
         responses = {
               @ApiResponse(responseCode = "200", description = "Token issued",
                     content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = AuthResponse.class))),
               @ApiResponse(responseCode = "401", description = "Invalid credentials")
         })
   @PostMapping("/tokens")
   public AuthResponse issueToken(@Valid @RequestBody final AuthRequest authRequest) {
      final Authentication authentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));

      final ApplicationUserDetails principal = (ApplicationUserDetails) authentication.getPrincipal();
      final List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
            .toList();

      final Map<String, Object> claims = new HashMap<>();
      claims.put("userId", principal.userId());
      claims.put("userType", principal.type().name());

      final String token = this.jwtService.issue(principal.getUsername(), roles, claims);
      return new AuthResponse(token, "Bearer", this.jwtService.getTtlSeconds());
   }
}
