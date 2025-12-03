package gr.hua.dit.project.mycitygov.core.service.model;

import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.model.UserType;

/**
 * CreateUserRequest (DTO).
 */
public record CreateUserRequest(
        String afm,
        String amka,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String passwordHash,
        UserType type,
        ServiceDepartment serviceDepartment
    ) {}


