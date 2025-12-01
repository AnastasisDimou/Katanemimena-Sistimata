package gr.hua.dit.project.mycitygov.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "request_types")
public class RequestType {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true, length = 100)
   private String protocolNumber;

   @Column(nullable = false, length = 100)
   private String name;

   @Column(length = 500)
   private String description;

   @Column(nullable = false)
   private boolean active = true;

   private Integer maxProcessingDays;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "department_id", nullable = false)
   private ServiceDepartment serviceDepartment;

   public RequestType() {
   }

   public RequestType(Long id, String protocolNumber, String name,
         String description, boolean active,
         Integer maxProcessingDays, ServiceDepartment serviceDepartment) {
      this.id = id;
      this.protocolNumber = protocolNumber;
      this.name = name;
      this.description = description;
      this.active = active;
      this.maxProcessingDays = maxProcessingDays;
      this.serviceDepartment = serviceDepartment;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getProtocolNumber() {
      return protocolNumber;
   }

   public void setProtocolNumber(String protocolNumber) {
      this.protocolNumber = protocolNumber;
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

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public Integer getMaxProcessingDays() {
      return maxProcessingDays;
   }

   public void setMaxProcessingDays(Integer maxProcessingDays) {
      this.maxProcessingDays = maxProcessingDays;
   }

   public ServiceDepartment getServiceDepartment() {
      return serviceDepartment;
   }

   public void setServiceDepartment(ServiceDepartment serviceDepartment) {
      this.serviceDepartment = serviceDepartment;
   }

   @Override
   public String toString() {
      return "RequestType{" +
            "id=" + id +
            ", protocolNumber='" + protocolNumber + '\'' +
            ", name='" + name + '\'' +
            ", active=" + active +
            ", maxProcessingDays=" + maxProcessingDays +
            '}';
   }
}
