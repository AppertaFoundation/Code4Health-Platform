package cloud.operon.platform.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A specification for form data
 */
@Embeddable
public class FormData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Embedded
    @Cascade(CascadeType.ALL)
    List<FormItem> formItems;
    String subTitle;
    @ElementCollection
    @Cascade(CascadeType.ALL)
    Map<String, String> conditionalBody;
    String header;
    String conditionalSubTitle;
    String footer;
    String title;

    public List<FormItem> getFormItems() {
        return formItems;
    }

    public void setFormItems(List<FormItem> formItems) {
        this.formItems = formItems;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Map<String, String> getConditionalBody() {
        return conditionalBody;
    }

    public void setConditionalBody(Map<String, String> conditionalBody) {
        this.conditionalBody = conditionalBody;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getConditionalSubTitle() {
        return conditionalSubTitle;
    }

    public void setConditionalSubTitle(String conditionalSubTitle) {
        this.conditionalSubTitle = conditionalSubTitle;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
