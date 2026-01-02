package gr.hua.dit.project.mycitygov.core.service.model;

import java.time.LocalDateTime;

import gr.hua.dit.project.mycitygov.core.model.RequestStatus;

public record TicketDetailsView(
      Long id,
      String protocolNumber,
      RequestStatus status,
      String subject,
      DepartmentSummary department,
      LocalDateTime submittedAt,
      LocalDateTime slaDeadline,
      LocalDateTime completedAt,
      String content,
      String attachmentUrl,
      UserSummary assignedEmployee,
      String employeeComments) {
}
