package gr.hua.dit.project.mycitygov.core.port;

import gr.hua.dit.project.mycitygov.core.port.impl.dto.PhoneNumberValidationResult;

public interface PhoneNumberPort {
   PhoneNumberValidationResult validatePhoneNumber(final String rawPhoneNumber);
}
