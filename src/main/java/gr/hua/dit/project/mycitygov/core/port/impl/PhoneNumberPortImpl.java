package gr.hua.dit.project.mycitygov.core.port.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import gr.hua.dit.project.mycitygov.config.RestApiClientConfig;
import gr.hua.dit.project.mycitygov.core.port.PhoneNumberPort;
import gr.hua.dit.project.mycitygov.core.port.impl.dto.PhoneNumberValidationResult;

@Service
public class PhoneNumberPortImpl implements PhoneNumberPort {

   private final RestTemplate restTemplate;

   private final RestApiClientConfig restApiClientConfig;

   public PhoneNumberPortImpl(
         final RestTemplate restTemplate,
         final RestApiClientConfig restApiClientConfig) {
      if (restTemplate == null)
         throw new NullPointerException();
      if (restApiClientConfig == null)
         throw new NullPointerException();
      this.restTemplate = restTemplate;
      this.restApiClientConfig = restApiClientConfig;
   }

   @Override
   public PhoneNumberValidationResult validatePhoneNumber(final String rawPhoneNumber) {
      if (rawPhoneNumber == null)
         throw new NullPointerException();
      if (rawPhoneNumber.isBlank())
         throw new IllegalArgumentException();

      final String baseUrl = restApiClientConfig.getBaseUrl();
      final String url = baseUrl + "/api/v1/phone-numbers/" + rawPhoneNumber + "/validations";

      final ResponseEntity<PhoneNumberValidationResult> response = this.restTemplate.getForEntity(url,
            PhoneNumberValidationResult.class);

      if (response.getStatusCode().is2xxSuccessful()) {
         final PhoneNumberValidationResult body = response.getBody();
         if (body == null)
            throw new NullPointerException();
         return body;
      }

      throw new RuntimeException(
            "External service responded with " + response.getStatusCode());
   }
}
