package cloud.operon.platform.domain;

import cloud.operon.platform.domain.enumeration.NotificationStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Set;

/**
 * A generic class representing a notification
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "notification")
public class Notification {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    Set<String> recipients;

    @ElementCollection
    Set<String> confirmationReceivers;

    String body;

    String subject;

    String recordComponentId;

    @ManyToOne
    Operino operino;

    NotificationStatus status;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}
