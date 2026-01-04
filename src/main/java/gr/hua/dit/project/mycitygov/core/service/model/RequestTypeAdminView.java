package gr.hua.dit.project.mycitygov.core.service.model;

public record RequestTypeAdminView(
      Long id,
      String protocolNumber,
      String name,
      String description,
      Integer maxProcessingDays,
      boolean active,
      Long departmentId,
      String departmentName) {
}
