package gr.hua.dit.project.mycitygov.core.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for requesting the creation (registration) of a User (Citizen).
 */
public record CreateUserRequest(

                @NotBlank @Size(min = 9, max = 9) String afm,

                @NotBlank @Size(min = 11, max = 11) String amka,

                @NotBlank @Size(max = 100) String firstName,

                @NotBlank @Size(max = 100) String lastName,

                @NotBlank @Email @Size(max = 100) String email,

                @NotBlank @Size(min = 10, max = 15) String phoneNumber,

                @NotBlank @Size(min = 8, max = 64) String rawPassword) {
}
