package gr.hua.dit.project.mycitygov.core.port.exception;

public class GovAuthException extends RuntimeException {

   public enum Reason {
      INVALID_CREDENTIALS,
      SERVICE_UNAVAILABLE
   }

   private final Reason reason;

   public GovAuthException(final Reason reason, final String message) {
      super(message);
      if (reason == null)
         throw new NullPointerException();
      this.reason = reason;
   }

   public GovAuthException(final Reason reason, final String message, final Throwable cause) {
      super(message, cause);
      if (reason == null)
         throw new NullPointerException();
      this.reason = reason;
   }

   public Reason reason() {
      return reason;
   }
}
