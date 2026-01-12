package gr.hua.dit.project.mycitygov.web.ui.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreateForm(
      @NotNull(message = "Επιλέξτε υπηρεσία.") Long departmentId,
      @NotNull(message = "Επιλέξτε ημερομηνία και ώρα.")
      @Future(message = "Η ημερομηνία και ώρα πρέπει να είναι στο μέλλον.")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime) {
}
