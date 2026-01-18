package gr.hua.dit.project.mycitygov.core.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for requesting the creation (registration) of a User (Citizen).
 */
public record CreateUserRequest(

                @NotBlank(message = "Το ΑΦΜ είναι υποχρεωτικό.")
                @Size(min = 9, max = 9, message = "Το ΑΦΜ πρέπει να έχει 9 ψηφία.")
                String afm,

                @NotBlank(message = "Το ΑΜΚΑ είναι υποχρεωτικό.")
                @Size(min = 11, max = 11, message = "Το ΑΜΚΑ πρέπει να έχει 11 ψηφία.")
                String amka,

                @NotBlank(message = "Το όνομα είναι υποχρεωτικό.")
                @Size(max = 100, message = "Το όνομα πρέπει να έχει έως 100 χαρακτήρες.")
                String firstName,

                @NotBlank(message = "Το επώνυμο είναι υποχρεωτικό.")
                @Size(max = 100, message = "Το επώνυμο πρέπει να έχει έως 100 χαρακτήρες.")
                String lastName,

                @NotBlank(message = "Το email είναι υποχρεωτικό.")
                @Email(message = "Το email δεν είναι έγκυρο.")
                @Size(max = 100, message = "Το email πρέπει να έχει έως 100 χαρακτήρες.")
                String email,

                @NotBlank(message = "Το τηλέφωνο είναι υποχρεωτικό.")
                @Size(min = 10, max = 10, message = "Ο αριθμός τηλεφώνου πρέπει να έχει 10 ψηφία.")
                String phoneNumber,

                @NotBlank(message = "Ο κωδικός είναι υποχρεωτικός.")
                @Size(min = 8, max = 64, message = "Ο κωδικός πρέπει να είναι μεταξύ 8 και 64 χαρακτήρων.")
                String rawPassword) {
}
