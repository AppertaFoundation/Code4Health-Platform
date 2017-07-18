package cloud.operon.platform.domain;

import cloud.operon.platform.domain.enumeration.NotificationStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;

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

    @Embedded
    Email email;

    @Embedded
    FormData formData;

    String recordComponentId;

    @ManyToOne
    Operino operino;

    NotificationStatus status;

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

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(FormData formData) {
        this.formData = formData;
    }
}
