package cloud.operon.platform.domain;

import javax.persistence.Embeddable;

/**
 * A generic spec for Email header
 */
@Embeddable
public class EmailHeader {

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
