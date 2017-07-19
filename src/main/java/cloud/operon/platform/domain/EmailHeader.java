package cloud.operon.platform.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * A generic spec for Email header
 */
@Embeddable
public class EmailHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    String subject;
    String body;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
