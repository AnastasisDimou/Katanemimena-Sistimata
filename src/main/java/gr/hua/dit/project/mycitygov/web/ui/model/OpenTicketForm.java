package gr.hua.dit.project.mycitygov.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OpenTicketForm(
      @NotNull Long requestTypeId,
      @NotBlank @Size(max = 200) String subject,
      @NotBlank @Size(max = 2000) String content,
      @Size(max = 500) String attachmentUrl) {
}
