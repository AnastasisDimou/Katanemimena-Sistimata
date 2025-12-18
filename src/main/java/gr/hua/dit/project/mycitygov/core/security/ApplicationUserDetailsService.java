package gr.hua.dit.project.mycitygov.core.security;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null)
            throw new NullPointerException();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null)
            throw new NullPointerException();
        if (username.isBlank())
            throw new IllegalArgumentException();

        final User user = this.userRepository
                .findByEmailIgnoreCase(username)
                .orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User with email " + username + " does not exist");
        }

        return new ApplicationUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getUserType());
    }
}
