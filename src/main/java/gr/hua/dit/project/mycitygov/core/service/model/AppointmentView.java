package gr.hua.dit.project.mycitygov.core.service.model;

import java.time.LocalDateTime;

public record AppointmentView(
      Long id,
      String protocolNumber,
      DepartmentSummary department,
      LocalDateTime appointmentDateTime) {
}
