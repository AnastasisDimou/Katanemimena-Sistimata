package gr.hua.dit.project.mycitygov.core.port.impl;

import gr.hua.dit.project.mycitygov.core.port.GovAuthPort;
import gr.hua.dit.project.mycitygov.core.port.exception.GovAuthException;
import gr.hua.dit.project.mycitygov.core.port.impl.dto.GovCitizenResponseDto;
import gr.hua.dit.project.mycitygov.core.port.impl.dto.GovLoginRequestDto;
import gr.hua.dit.project.mycitygov.core.port.impl.dto.GovLoginResponseDto;
import gr.hua.dit.project.mycitygov.core.port.model.GovCitizen;
import gr.hua.dit.project.mycitygov.core.port.model.GovLoginResult;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class GovAuthPortImpl implements GovAuthPort {

   private static final Logger LOGGER = LoggerFactory.getLogger(GovAuthPortImpl.class);

   private final RestTemplate restTemplate;
   private final String nocBaseUrl;

   public GovAuthPortImpl(
         final RestTemplate restTemplate,
         @Value("${external.service.base-url}") final String nocBaseUrl) {
      if (restTemplate == null)
         throw new NullPointerException();
      if (nocBaseUrl == null)
         throw new NullPointerException();
      this.restTemplate = restTemplate;
      this.nocBaseUrl = nocBaseUrl;
   }

   @Override
   public GovLoginResult login(final String afm, final String pin) throws GovAuthException {
      if (afm == null)
         throw new NullPointerException("afm cannot be null");
      if (pin == null)
         throw new NullPointerException("pin cannot be null");

      final String url = this.nocBaseUrl + "/api/v1/gov/login";
      final GovLoginRequestDto requestDto = new GovLoginRequestDto(afm, pin);

      try {
         final ResponseEntity<GovLoginResponseDto> response = this.restTemplate.postForEntity(
               url,
               requestDto,
               GovLoginResponseDto.class);

         if (response.getStatusCode().is2xxSuccessful()) {
            return convertResponse(response.getBody());
         }

         if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new GovAuthException(GovAuthException.Reason.INVALID_CREDENTIALS,
                  "Invalid gov credentials");
         }

         throw new GovAuthException(GovAuthException.Reason.SERVICE_UNAVAILABLE,
               "Unexpected response from NOC: " + response.getStatusCode());
      } catch (HttpStatusCodeException ex) {
         if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new GovAuthException(GovAuthException.Reason.INVALID_CREDENTIALS,
                  "Invalid gov credentials", ex);
         }
         throw new GovAuthException(GovAuthException.Reason.SERVICE_UNAVAILABLE,
               "Failed to call NOC gov login", ex);
      } catch (ResourceAccessException ex) {
         throw new GovAuthException(GovAuthException.Reason.SERVICE_UNAVAILABLE,
               "NOC gov login unreachable", ex);
      }
   }

   private GovLoginResult convertResponse(final GovLoginResponseDto dto) {
      if (dto == null) {
         throw new GovAuthException(GovAuthException.Reason.SERVICE_UNAVAILABLE,
               "Empty response from NOC gov login");
      }

      final GovCitizenResponseDto citizenDto = dto.citizen();
      if (citizenDto == null) {
         throw new GovAuthException(GovAuthException.Reason.SERVICE_UNAVAILABLE,
               "Missing citizen info in NOC response");
      }

      final Instant expiresAt = parseInstant(dto.expiresAt());
      final GovCitizen citizen = new GovCitizen(
            citizenDto.afm(),
            citizenDto.amka(),
            citizenDto.fullName());

      return new GovLoginResult(dto.token(), expiresAt, citizen);
   }

   private Instant parseInstant(final String value) {
      if (value == null || value.isBlank()) {
         return null;
      }
      try {
         return Instant.parse(value);
      } catch (DateTimeParseException ex) {
         LOGGER.warn("Failed to parse expiresAt '{}' from NOC response", value);
         return null;
      }
   }
}
