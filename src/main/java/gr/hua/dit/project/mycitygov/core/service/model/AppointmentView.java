package gr.hua.dit.project.mycitygov.core.service.model;

import java.time.LocalDateTime;

import gr.hua.dit.project.mycitygov.core.model.AppointmentStatus;

public record AppointmentView(
      Long id,
      String protocolNumber,
      DepartmentSummary department,
      LocalDateTime appointmentDateTime,
      AppointmentStatus status) {
}
