package cloud.operon.platform.domain;

import java.util.Set;

/**
 * A generic class representing a notification
 */
public class Notification {

    Set<String> recipients;

    Set<String> confirmationReceivers;

    String body;

    String subject;

    String recordComponentId;

    Operino operino;

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

    public String getRecordComponentId() {
        return recordComponentId;
    }

    public void setRecordComponentId(String recordComponentId) {
        this.recordComponentId = recordComponentId;
    }

    public Operino getOperino() {
        return operino;
    }

    public void setOperino(Operino operino) {
        this.operino = operino;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
