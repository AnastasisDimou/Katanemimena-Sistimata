package gr.hua.dit.project.mycitygov.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Person entity.
 */
@Entity
@Table(name = "app_users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "afm", nullable = false, length = 20, unique = true)
   private String afm;

   @Column(name = "amka", nullable = false, length = 20, unique = true)
   private String amka;

   @Column(name = "first_name", nullable = false, length = 100)
   private String firstName;

   @Column(name = "last_name", nullable = false, length = 100)
   private String lastName;

   @Column(name = "email", nullable = false, unique = true, length = 100)
   private String email;

   @Column(name = "phone_number", nullable = false, unique = true, length = 18)
   private String phoneNumber;

   @Column(name = "password_hash", nullable = false)
   private String passwordHash;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private UserType userType;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "department_id", nullable = true)
   private ServiceDepartment serviceDepartment;

   @CreationTimestamp
   @Column(name = "created_at", nullable = false, updatable = false)
   private Instant createdAt;

   public User() {
   }

   public User(Long id, String afm, String amka,
         String firstName, String lastName,
         String email, String phoneNumber,
         String passwordHash, UserType userType) {
      this.id = id;
      this.afm = afm;
      this.amka = amka;
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.phoneNumber = phoneNumber;
      this.passwordHash = passwordHash;
      this.userType = userType;
      this.createdAt = Instant.now();
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getAfm() {
      return afm;
   }

   public void setAfm(String afm) {
      this.afm = afm;
   }

   public String getAmka() {
      return amka;
   }

   public void setAmka(String amka) {
      this.amka = amka;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getPasswordHash() {
      return passwordHash;
   }

   public void setPasswordHash(String passwordHash) {
      this.passwordHash = passwordHash;
   }

   public UserType getUserType() {
      return userType;
   }

   public void setUserType(UserType userType) {
      this.userType = userType;
   }

   public ServiceDepartment getServiceDepartment() {
      return serviceDepartment;
   }

   public void setServiceDepartment(ServiceDepartment serviceDepartment) {
      this.serviceDepartment = serviceDepartment;
   }

   public Instant getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Instant createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public String toString() {
      return "User{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", userType=" + userType +
            '}';
   }
}