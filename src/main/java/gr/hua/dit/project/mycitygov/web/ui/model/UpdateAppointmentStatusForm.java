package gr.hua.dit.project.mycitygov.web.ui.model;

import gr.hua.dit.project.mycitygov.core.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAppointmentStatusForm(
      @NotNull AppointmentStatus status) {
}
