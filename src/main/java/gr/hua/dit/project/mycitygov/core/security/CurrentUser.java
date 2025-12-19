package gr.hua.dit.project.mycitygov.core.security;

import gr.hua.dit.project.mycitygov.core.model.UserType;

public record CurrentUser(
      String firstName,
      String lastName,
      String email,
      UserType type) {
}
