package gr.hua.dit.project.mycitygov.web.ui.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreateForm(
      @NotNull Long departmentId,
      @NotNull @Future @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime) {
}
