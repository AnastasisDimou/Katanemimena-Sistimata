package gr.hua.dit.project.mycitygov.core.security;

import gr.hua.dit.project.mycitygov.core.model.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Immutable view implementing Spring's {@link UserDetails} for representing a
 * user in runtime.
 */
public final class ApplicationUserDetails implements UserDetails {

   private final long userId;
   private final String email;
   private final String hashedPassword;
   private final UserType type;

   public ApplicationUserDetails(
         final long userId,
         final String email,
         final String hashedPassword,
         final UserType type) {
      if (userId <= 0)
         throw new IllegalArgumentException("User ID must be positive");
      if (email == null || email.isBlank())
         throw new IllegalArgumentException("Email cannot be null or blank");
      if (hashedPassword == null || hashedPassword.isBlank())
         throw new IllegalArgumentException("Password hash cannot be null or blank");
      if (type == null)
         throw new NullPointerException("UserType cannot be null");

      this.userId = userId;
      this.email = email;
      this.hashedPassword = hashedPassword;
      this.type = type;
   }

   public long userId() {
      return this.userId;
   }

   public UserType type() {
      return this.type;
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      final String role;
      if (this.type == UserType.EMPLOYEE)
         role = "ROLE_EMPLOYEE";
      else if (this.type == UserType.CITIZEN)
         role = "ROLE_CITIZEN";
      else if (this.type == UserType.ADMIN)
         role = "ROLE_ADMIN";
      else
         throw new RuntimeException("Invalid type: " + this.type);
      return Collections.singletonList(new SimpleGrantedAuthority(role));
   }

   @Override
   public String getPassword() {
      return this.hashedPassword;
   }

   @Override
   public String getUsername() {
      return this.email;
   }

   @Override
   public boolean isAccountNonExpired() {
      return true;
   }

   @Override
   public boolean isAccountNonLocked() {
      return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}