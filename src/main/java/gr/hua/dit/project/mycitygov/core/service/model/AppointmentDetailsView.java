package gr.hua.dit.project.mycitygov.core.service.model;

import java.time.LocalDateTime;

public record AppointmentDetailsView(
      Long id,
      String protocolNumber,
      DepartmentSummary department,
      String departmentDescription,
      LocalDateTime appointmentDateTime,
      UserSummary citizen,
      String citizenPhoneNumber) {
}
