package gr.hua.dit.project.mycitygov.core.port.impl.dto;

public record GovLoginResponseDto(
      String token,
      String expiresAt,
      GovCitizenResponseDto citizen) {
}
