package gr.hua.dit.project.mycitygov.core.service.model;


import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.model.UserType;

/**
 * UserView (DTO) that includes only information to be exposed.
 */
public record UserView(
        long id,
        String afm,
        String amka,
        String firstName,
        String lastName,
        String email,
        UserType type,
        ServiceDepartment serviceDepartment
) {
    public String fullName() {
        return this.firstName + " " + this.lastName;
    }
}
