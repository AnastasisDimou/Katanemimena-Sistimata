package gr.hua.dit.project.mycitygov.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 20, unique = true)
   private String afm;

   @Column(nullable = false, length = 20, unique = true)
   private String amka;

   @Column(nullable = false, length = 100)
   private String firstName;

   @Column(nullable = false, length = 100)
   private String lastName;

   @Column(nullable = false, unique = true, length = 100)
   private String email;

   @Column(nullable = false, unique = true, length = 18)
   private String phoneNumber;

   @Column(nullable = false)
   private String passwordHash;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private UserType userType;

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
