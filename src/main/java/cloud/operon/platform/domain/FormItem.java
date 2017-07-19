package cloud.operon.platform.domain;

import org.hibernate.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * A representation of a form item
 */
@Embeddable
public class FormItem implements Serializable {

    private static final long serialVersionUID = 1L;

    String label;
    String value;
    String comment;
    @OneToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
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
