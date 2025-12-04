package gr.hua.dit.project.mycitygov.core.service;

import gr.hua.dit.project.mycitygov.core.service.model.CreateUserRequest;
import gr.hua.dit.project.mycitygov.core.service.model.CreateUserResult;
import gr.hua.dit.project.mycitygov.core.service.model.UserView;

import java.util.List;

/**
 * Service (contract) for managing user types
 */
public interface UserService {

   List<UserView> getUsers();

    CreateUserResult createUser(final CreateUserRequest createUserRequest);

}