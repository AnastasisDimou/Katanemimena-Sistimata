package gr.hua.dit.project.mycitygov.core.service;

import gr.hua.dit.project.mycitygov.core.service.model.CreateUserRequest;
import gr.hua.dit.project.mycitygov.core.service.model.CreateUserResult;

public interface UserBusinessLogicService {
   CreateUserResult createUser(CreateUserRequest createUserRequest, final boolean notify);

   default CreateUserResult createUser(final CreateUserRequest createPersonRequest) {
      return this.createUser(createPersonRequest, true);
   }
}
