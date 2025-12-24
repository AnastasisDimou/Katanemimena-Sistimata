package gr.hua.dit.project.mycitygov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for MyCityGov (UI, cookie-based).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

      @Bean
      @Order(0)
      public SecurityFilterChain apiChain(final HttpSecurity http) throws Exception {
            http
                        .securityMatcher("/api/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated())
                        .httpBasic(Customizer.withDefaults())
                        .formLogin(AbstractHttpConfigurer::disable);

            return http.build();
      }

      @Bean
      @Order(1)
      public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
            http
                        .securityMatcher("/**")
                        .csrf(csrf -> csrf
                                    .ignoringRequestMatchers("/auth/gov/login", "/h2-console/**"))

                        .authorizeHttpRequests(auth -> auth
                                    // Public endpoints (no login required)
                                    .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**",
                                                "/h2-console/**")
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
                                    // Use default POST /logout endpoint
                                    .logoutUrl("/logout")
                                    .logoutSuccessUrl("/login?logout")
                                    .deleteCookies("JSESSIONID")
                                    .invalidateHttpSession(true)
                                    .permitAll())

                        .httpBasic(AbstractHttpConfigurer::disable);

            // Allow H2 console to render in a frame
            http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

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
