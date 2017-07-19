package cloud.operon.platform.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * A representation of email
 */

@Embeddable
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;

    @ElementCollection
    @Cascade(CascadeType.ALL)
    Set<String> recipients;

    @ElementCollection
    @Cascade(CascadeType.ALL)
    Set<String> confirmationReceivers;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="body",column=@Column(name="confirm_body")),
            @AttributeOverride(name="subject",column=@Column(name="confirm_subject"))
    })
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    EmailHeader confirmationEmail;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="body",column=@Column(name="report_body")),
            @AttributeOverride(name="subject",column=@Column(name="report_subject"))
    })
    @Cascade(CascadeType.ALL)
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
