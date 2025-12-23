package gr.hua.dit.project.mycitygov.core.service;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.port.GovAuthPort;
import gr.hua.dit.project.mycitygov.core.port.model.GovCitizen;
import gr.hua.dit.project.mycitygov.core.port.model.GovLoginResult;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import gr.hua.dit.project.mycitygov.core.service.model.GovLoginOutcome;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GovLoginService {

   private final GovAuthPort govAuthPort;
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;

   public GovLoginService(
         final GovAuthPort govAuthPort,
         final UserRepository userRepository,
         final PasswordEncoder passwordEncoder) {
      if (govAuthPort == null)
         throw new NullPointerException();
      if (userRepository == null)
         throw new NullPointerException();
      if (passwordEncoder == null)
         throw new NullPointerException();
      this.govAuthPort = govAuthPort;
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
   }

   @Transactional
   public GovLoginOutcome loginWithGov(final String afm, final String pin) {
      if (afm == null)
         throw new NullPointerException("afm cannot be null");
      if (pin == null)
         throw new NullPointerException("pin cannot be null");

      final String normalizedAfm = afm.strip();
      final String normalizedPin = pin.strip();

      final GovLoginResult govLoginResult = this.govAuthPort.login(normalizedAfm, normalizedPin);
      final User user = this.findOrCreateUser(govLoginResult.citizen());

      return new GovLoginOutcome(user, govLoginResult);
   }

   private User findOrCreateUser(final GovCitizen citizen) {
      if (citizen == null)
         throw new NullPointerException("citizen cannot be null");
      if (citizen.afm() == null || citizen.afm().isBlank()) {
         throw new IllegalArgumentException("citizen afm is required");
      }

      final User existing = this.userRepository.findByAfm(citizen.afm()).orElse(null);
      if (existing != null) {
         return existing;
      }

      return this.createUserFromGovCitizen(citizen);
   }

   private User createUserFromGovCitizen(final GovCitizen citizen) {
      final NameParts nameParts = this.splitFullName(citizen.fullName());

      final User user = new User();
      user.setAfm(citizen.afm());
      user.setAmka(this.normalizeAmka(citizen));
      user.setFirstName(nameParts.firstName());
      user.setLastName(nameParts.lastName());
      user.setEmail(this.generateGovEmail(citizen.afm()));
      user.setPhoneNumber(this.generateGovPhone(citizen.afm()));
      user.setPasswordHash(this.passwordEncoder.encode(this.generateRandomPassword()));
      user.setUserType(UserType.CITIZEN);

      return this.userRepository.save(user);
   }

   private String normalizeAmka(final GovCitizen citizen) {
      if (citizen.amka() != null && !citizen.amka().isBlank()) {
         return citizen.amka().strip();
      }
      return citizen.afm();
   }

   private String generateGovEmail(final String afm) {
      return "gov-" + afm + "@mycitygov.local";
   }

   private String generateGovPhone(final String afm) {
      return "GOV-" + afm;
   }

   private String generateRandomPassword() {
      return "gov-" + UUID.randomUUID();
   }

   private NameParts splitFullName(final String fullName) {
      if (fullName == null || fullName.isBlank()) {
         return new NameParts("Gov", "User");
      }

      final String trimmed = fullName.trim();
      final int idx = trimmed.lastIndexOf(' ');

      if (idx <= 0) {
         return new NameParts(trimmed, "Gov");
      }

      final String first = trimmed.substring(0, idx).trim();
      final String last = trimmed.substring(idx + 1).trim();

      final String safeFirst = first.isEmpty() ? "Gov" : first;
      final String safeLast = last.isEmpty() ? "User" : last;

      return new NameParts(safeFirst, safeLast);
   }

   private record NameParts(String firstName, String lastName) {
   }
}
