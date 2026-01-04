package gr.hua.dit.project.mycitygov.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentForm(
      @NotBlank @Size(max = 100) String code,
      @NotBlank @Size(max = 200) String name,
      @Size(max = 500) String description) {
}
