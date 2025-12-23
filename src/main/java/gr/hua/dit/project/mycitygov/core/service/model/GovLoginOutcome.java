package gr.hua.dit.project.mycitygov.core.service.model;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.port.model.GovLoginResult;

public record GovLoginOutcome(
      User user,
      GovLoginResult govLoginResult) {
}
