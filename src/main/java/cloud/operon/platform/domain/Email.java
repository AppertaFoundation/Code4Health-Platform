package cloud.operon.platform.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Set;

/**
 * A representation of email
 */

@Embeddable
public class Email {

    @ElementCollection
    Set<String> recipients;

    @ElementCollection
    Set<String> confirmationReceivers;

    @Embedded
    EmailHeader confirmationEmail;

    @Embedded
    EmailHeader reportEmail;

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    public Set<String> addRecipient(String recipient) {
        this.recipients.add(recipient);
        return this.recipients;
    }

    public Set<String> removeRecipient(String recipient) {
        this.recipients.remove(recipient);
        return this.recipients;
    }

    public Set<String> getConfirmationReceivers() {
        return confirmationReceivers;
    }

    public void setConfirmationReceivers(Set<String> confirmationReceivers) {
        this.confirmationReceivers = confirmationReceivers;
    }

    public Set<String> addConfirmationReceiver(String confirmationReceiever) {
        this.confirmationReceivers.add(confirmationReceiever);
        return this.confirmationReceivers;
    }

    public Set<String> removeConfirmationReceiver(String confirmationReceiever) {
        this.confirmationReceivers.remove(confirmationReceiever);
        return this.confirmationReceivers;
    }

    public EmailHeader getConfirmationEmail() {
        return confirmationEmail;
    }

    public void setConfirmationEmail(EmailHeader confirmationEmail) {
        this.confirmationEmail = confirmationEmail;
    }

    public EmailHeader getReportEmail() {
        return reportEmail;
    }

    public void setReportEmail(EmailHeader reportEmail) {
        this.reportEmail = reportEmail;
    }
}
