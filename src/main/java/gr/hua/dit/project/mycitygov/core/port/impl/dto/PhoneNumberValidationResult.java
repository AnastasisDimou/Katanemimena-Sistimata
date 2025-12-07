package gr.hua.dit.project.mycitygov.core.port.impl.dto;

public record PhoneNumberValidationResult(
      String raw,
      boolean valid,
      String type,
      String e164) {
   public boolean isValid() {
      return valid;
   }

   public boolean isValidMobile() {
      if (!valid || type == null)
         return false;
      return "mobile".equals(type);
   }
}
