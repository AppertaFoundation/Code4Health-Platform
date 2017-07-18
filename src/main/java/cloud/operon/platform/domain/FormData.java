package cloud.operon.platform.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * A specification for form data
 */
@Embeddable
public class FormData {


    @Embedded
    Set<FormItem> formItems;
    String subTitle;
    List<String> conditionalBody;
    String header;
    String conditionalSubTitle;
    String footer;
    String title;

    public Set<FormItem> getFormItems() {
        return formItems;
    }

    public void setFormItems(Set<FormItem> formItems) {
        this.formItems = formItems;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<String> getConditionalBody() {
        return conditionalBody;
    }

    public void setConditionalBody(List<String> conditionalBody) {
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

    @Embeddable
    class FormItem {

        String label;
        String value;
        String comment;
        @OneToMany
        Set<FormItem> childItems;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Set<FormItem> getChildItems() {
            return childItems;
        }

        public void setChildItems(Set<FormItem> childItems) {
            this.childItems = childItems;
        }
    }
}
