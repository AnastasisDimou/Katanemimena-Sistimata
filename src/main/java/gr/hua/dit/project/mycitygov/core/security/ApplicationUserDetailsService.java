package gr.hua.dit.project.mycitygov.core.security;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring's {@code UserDetailsService} for providing application users.
 * Loads user details required for authentication (username/password check) and context creation.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null) throw new NullPointerException();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) throw  new IllegalArgumentException();

        // 1. Fetch the User entity from the database using the email (username)
        final User user = this.userRepository
                .findByEmailIgnoreCase(username.strip())
                .orElseThrow(() -> new UsernameNotFoundException("User with emailAddress " + username + " does not exist"));

        // 2. Map the entity to the security DTO (ApplicationUserDetails)
        return new ApplicationUserDetails(
                user.getId(),
                user.getAfm(),
                user.getAmka(),
                user.getEmail(),
                user.getPassword(),
                user.getUserType(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}