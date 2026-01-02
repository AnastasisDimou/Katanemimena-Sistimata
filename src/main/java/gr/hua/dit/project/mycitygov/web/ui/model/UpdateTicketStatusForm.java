package gr.hua.dit.project.mycitygov.web.ui.model;

import gr.hua.dit.project.mycitygov.core.model.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTicketStatusForm(
      @NotNull RequestStatus newStatus,
      @Size(max = 2000) String comments) {
}
