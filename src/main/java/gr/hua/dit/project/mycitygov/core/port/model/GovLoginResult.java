package gr.hua.dit.project.mycitygov.core.port.model;

import java.time.Instant;

public record GovLoginResult(
      String token,
      Instant expiresAt,
      GovCitizen citizen) {
}
