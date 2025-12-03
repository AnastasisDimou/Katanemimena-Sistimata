package gr.hua.dit.project.mycitygov.core.service.impl;


import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import gr.hua.dit.project.mycitygov.core.service.UserService;
import gr.hua.dit.project.mycitygov.core.service.model.CreateUserRequest;
import gr.hua.dit.project.mycitygov.core.service.model.UserView;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        if  (userRepository == null) throw new IllegalArgumentException("UserRepository cannot be null");
        this.userRepository = userRepository;
    }

    @Override
    public List<UserView> getUsers() {
        return List.of();
    }

    @Override
    public UserView createUser(final CreateUserRequest createUserRequest) {
        if (createUserRequest == null) throw new IllegalArgumentException("createUserRequest cannot be null");

        // Unpack
        final String afm = createUserRequest.afm().strip();
        final String amka = createUserRequest.amka().strip();
        final String firstName = createUserRequest.firstName().strip();
        final String lastName = createUserRequest.lastName().strip();
        final String email = createUserRequest.email().strip();
        final String phoneNumber = createUserRequest.phoneNumber().strip();
        final String rawPassword = createUserRequest.passwordHash();
        final UserType type = createUserRequest.type();


        // Encode password (raw to hashed).
        final String hashedPassword = rawPassword;

        User user = new User();
        user.setId(null); // auto-generated
        user.setAfm(afm);
        user.setAmka(amka);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setPhoneNumber(phoneNumber);
        user.setCreatedAt(null); // auto-generated

        // Save/insert user to db
        user = this.userRepository.save(user);

        // Map user to UserView
        final UserView userView = new UserView();

        return userView;
    }
}
