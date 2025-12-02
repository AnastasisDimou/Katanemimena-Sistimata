package gr.hua.dit.project.mycitygov.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   // reference number for the citizen
   @Column(nullable = false, unique = true, length = 100)
   private String protocolNumber;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "citizen_id", nullable = false)
   private User citizen;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "request_type_id", nullable = false)
   private RequestType requestType;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 30)
   private RequestStatus status;

   @Column(nullable = false)
   private LocalDateTime submissionDate;

   @Column(nullable = false)
   private LocalDateTime dueDate;

   private LocalDateTime completionDate;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "assigned_employee_id")
   private User assignedEmployee;

   @Column(length = 2000)
   private String description;

   public Request() {
   }

   public Request(String protocolNumber,
         User citizen,
         RequestType requestType,
         RequestStatus status,
         LocalDateTime submissionDate,
         LocalDateTime dueDate,
         LocalDateTime completionDate,
         User assignedEmployee,
         String description) {
      this.protocolNumber = protocolNumber;
      this.citizen = citizen;
      this.requestType = requestType;
      this.status = status;
      this.submissionDate = submissionDate;
      this.dueDate = dueDate;
      this.completionDate = completionDate;
      this.assignedEmployee = assignedEmployee;
      this.description = description;
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

   public RequestType getRequestType() {
      return requestType;
   }

   public void setRequestType(RequestType requestType) {
      this.requestType = requestType;
   }

   public RequestStatus getStatus() {
      return status;
   }

   public void setStatus(RequestStatus status) {
      this.status = status;
   }

   public LocalDateTime getSubmissionDate() {
      return submissionDate;
   }

   public void setSubmissionDate(LocalDateTime submissionDate) {
      this.submissionDate = submissionDate;
   }

   public LocalDateTime getDueDate() {
      return dueDate;
   }

   public void setDueDate(LocalDateTime dueDate) {
      this.dueDate = dueDate;
   }

   public LocalDateTime getCompletionDate() {
      return completionDate;
   }

   public void setCompletionDate(LocalDateTime completionDate) {
      this.completionDate = completionDate;
   }

   public User getAssignedEmployee() {
      return assignedEmployee;
   }

   public void setAssignedEmployee(User assignedEmployee) {
      this.assignedEmployee = assignedEmployee;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String toString() {
      return "Request{" +
            "id=" + id +
            ", protocolNumber='" + protocolNumber + '\'' +
            ", status=" + status +
            ", citizenId=" + (citizen != null ? citizen.getId() : null) +
            ", requestTypeId=" + (requestType != null ? requestType.getId() : null) +
            ", assignedEmployeeId=" + (assignedEmployee != null ? assignedEmployee.getId() : null) +
            '}';
   }
}
