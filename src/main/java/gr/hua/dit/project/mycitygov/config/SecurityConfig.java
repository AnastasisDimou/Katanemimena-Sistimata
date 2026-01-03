package gr.hua.dit.project.mycitygov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gr.hua.dit.project.mycitygov.core.security.JwtAuthenticationFilter;

/**
 * Security configuration for MyCityGov (UI, cookie-based).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

      @Bean
      @Order(1)
      public SecurityFilterChain apiChain(final HttpSecurity http,
                  final JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
            http
                        .securityMatcher("/api/**")
                        .csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                                    .requestMatchers("/api/auth/**").permitAll()
                                    .anyRequest().authenticated())
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .httpBasic(AbstractHttpConfigurer::disable)
                        .formLogin(AbstractHttpConfigurer::disable);

            return http.build();
      }

      @Bean
      @Order(2)
      public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
            http
                        .securityMatcher("/**")
                        .csrf(csrf -> csrf
                                    .ignoringRequestMatchers(
                                                "/auth/gov/login",
                                                "/h2-console/**",
                                                "/api/**",
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html"))

                        .authorizeHttpRequests(auth -> auth
                                    // Public endpoints (no login required)
                                    .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**",
                                                "/h2-console/**",
                                                "/v3/api-docs/**",
                                                "/swagger-ui.html",
                                                "/swagger-ui/**")
                                    .permitAll()

                                    // Pages that require authentication
                                    .requestMatchers("/profile", "/logout", "/tickets/**", "/appointments/**")
                                    .authenticated()

                                    // The rest are public
                                    .anyRequest().permitAll())

                        .formLogin(form -> form
                                    .loginPage("/login")
                                    .loginProcessingUrl("/login")
                                    .defaultSuccessUrl("/", true)
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
