package gr.hua.dit.project.mycitygov.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "service_departments")
public class ServiceDepartment {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true, length = 100)
   private String code; // e.g. "KEP", "TECHNICAL"

   @Column(nullable = false, unique = true, length = 200)
   private String name; // e.g. "ΚΕΠ Δήμου"

   @Column(length = 500)
   private String description;

   public ServiceDepartment() {
   }

   public ServiceDepartment(Long id, String code, String name, String description) {
      this.id = id;
      this.code = code;
      this.name = name;
      this.description = description;
   }

   // Getters & setters

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String toString() {
      return "ServiceDepartment{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            '}';
   }
}
