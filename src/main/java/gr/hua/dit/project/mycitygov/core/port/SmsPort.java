package gr.hua.dit.project.mycitygov.core.port;

/**
 * Port to external service for managing SMS notifications.
 */
public interface SmsPort {

    /**
     * Sends an SMS message.
     * @param e164 The phone number in E.164 format (e.g., +3069...).
     * @param content The message content.
     * @return true if sent successfully.
     */
    boolean sendSms(final String e164, final String content);
}