package gr.hua.dit.project.mycitygov.core.service;

import gr.hua.dit.project.mycitygov.core.service.model.CreateUserRequest;
import gr.hua.dit.project.mycitygov.core.service.model.UserView;

import java.util.List;

/**
 * Service (contract) for managing user types
 */
public interface UserService {

    List<UserView> getUsers();

    UserView createUser(CreateUserRequest createUserRequest);

}
