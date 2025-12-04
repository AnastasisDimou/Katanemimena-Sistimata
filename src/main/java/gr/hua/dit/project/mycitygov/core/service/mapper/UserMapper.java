package gr.hua.dit.project.mycitygov.core.service.mapper;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.service.model.UserView;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link User} to {@link UserView}.
 */
@Component
public class UserMapper {

    public UserView convertUserToUserView(final User user) {
        if (user == null) {
            return null;
        }

        return new UserView(
                user.getId(),
                user.getAfm(),
                user.getAmka(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserType(),
                user.getServiceDepartment()
        );
    }
}