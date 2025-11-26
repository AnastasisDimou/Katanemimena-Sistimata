package gr.hua.dit.project.mycitygov.core.model;

public class User {
   private Long id;
   private String afm;
   private String amka;
   private String firstName;
   private String lastName;
   private String email;
   private String passwordHash;
   private UserType userType;

   public User() {
   }

   public User(Long id, String afm, String amka,
         String firstName, String lastName,
         String email, String passwordHash,
         UserType userType) {
      this.id = id;
      this.afm = afm;
      this.amka = amka;
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
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
            ", userType=" + userType +
            '}';
   }

}
