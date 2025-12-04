package gr.hua.dit.project.mycitygov.core.service.model;


/**
 * CreateUserResult DTO.
 *
 * @see gr.hua.dit.project.mycitygov.core.service.impl.UserServiceImpl#createUser(CreateUserRequest)
 */
public record CreateUserResult(
        boolean created,
        String reason,
        UserView userView
) {

    public static CreateUserResult success(final UserView userView) {
        if (userView == null) throw new NullPointerException();
        return new CreateUserResult(true, null, userView);
    }

    public static CreateUserResult fail(final String reason) {
        if (reason == null) throw new NullPointerException();
        if (reason.isBlank()) throw new IllegalArgumentException();
        return new CreateUserResult(false, reason, null);
    }
}