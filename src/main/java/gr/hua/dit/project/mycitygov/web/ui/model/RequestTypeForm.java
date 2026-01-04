package gr.hua.dit.project.mycitygov.web.ui.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestTypeForm(
      @NotBlank @Size(max = 100) String protocolNumber,
      @NotBlank @Size(max = 100) String name,
      @Size(max = 500) String description,
      @Min(1) Integer maxProcessingDays,
      @NotNull Long departmentId) {
}
