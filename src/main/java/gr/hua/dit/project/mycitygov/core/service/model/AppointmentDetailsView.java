package gr.hua.dit.project.mycitygov.core.service.model;

import java.time.LocalDateTime;

import gr.hua.dit.project.mycitygov.core.model.AppointmentStatus;

public record AppointmentDetailsView(
      Long id,
      String protocolNumber,
      DepartmentSummary department,
      String departmentDescription,
      LocalDateTime appointmentDateTime,
      AppointmentStatus status,
      UserSummary citizen,
      String citizenPhoneNumber) {
}
