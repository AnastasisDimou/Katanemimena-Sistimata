package gr.hua.dit.project.mycitygov.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true, length = 100)
   private String protocolNumber;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "citizen_id", nullable = false)
   private User citizen;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "department_id", nullable = false)
   private ServiceDepartment serviceDepartment;

   @Column(nullable = false)
   private LocalDateTime appointmentDateTime;

   public Appointment() {
   }

   public Appointment(Long id,
         String protocolNumber,
         User citizen,
         ServiceDepartment serviceDepartment,
         LocalDateTime appointmentDateTime) {
      this.id = id;
      this.protocolNumber = protocolNumber;
      this.citizen = citizen;
      this.serviceDepartment = serviceDepartment;
      this.appointmentDateTime = appointmentDateTime;
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

   public User getCitizen() {
      return citizen;
   }

   public void setCitizen(User citizen) {
      this.citizen = citizen;
   }

   public ServiceDepartment getServiceDepartment() {
      return serviceDepartment;
   }

   public void setServiceDepartment(ServiceDepartment serviceDepartment) {
      this.serviceDepartment = serviceDepartment;
   }

   public LocalDateTime getAppointmentDateTime() {
      return appointmentDateTime;
   }

   public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
      this.appointmentDateTime = appointmentDateTime;
   }

   @Override
   public String toString() {
      return "Appointment{" +
            "id=" + id +
            ", protocolNumber='" + protocolNumber + '\'' +
            ", citizenId=" + (citizen != null ? citizen.getId() : null) +
            ", departmentId=" + (serviceDepartment != null ? serviceDepartment.getId() : null) +
            ", appointmentDateTime=" + appointmentDateTime +
            '}';
   }
}
