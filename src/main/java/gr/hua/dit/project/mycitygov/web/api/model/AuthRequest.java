package gr.hua.dit.project.mycitygov.web.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
      @Schema(example = "citizen@example.com") @Email @NotBlank String email,
      @Schema(example = "password") @NotBlank String password) {
}
