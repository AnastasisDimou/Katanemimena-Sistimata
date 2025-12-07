package gr.hua.dit.project.mycitygov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// TODO need to configure api security for external apps

/**
 * Security configuration for MyCityGov (UI, cookie-based).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

      @Bean
      @Order(1)
      public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
            http
                        .securityMatcher("/**")

                        .authorizeHttpRequests(auth -> auth
                                    // Public endpoints (no login required)
                                    .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**")
                                    .permitAll()

                                    // Pages that require authentication
                                    .requestMatchers("/profile", "/logout").authenticated()

                                    // The rest are public
                                    .anyRequest().permitAll())

                        .formLogin(form -> form
                                    .loginPage("/login")
                                    .loginProcessingUrl("/login")
                                    .defaultSuccessUrl("/profile", true)
                                    .failureUrl("/login?error")
                                    .permitAll())

                        .logout(logout -> logout
                                    .logoutUrl("/logout")
                                    .logoutSuccessUrl("/login?logout")
                                    .deleteCookies("JSESSIONID")
                                    .invalidateHttpSession(true)
                                    .permitAll())

                        .httpBasic(AbstractHttpConfigurer::disable);

            return http.build();
      }

      @Bean
      public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
      }

      @Bean
      public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
      }
}
