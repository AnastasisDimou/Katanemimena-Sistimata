package gr.hua.dit.project.mycitygov.web.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
      @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken,
      @Schema(example = "Bearer") String tokenType,
      @Schema(example = "86400") long expiresInSeconds) {
}
