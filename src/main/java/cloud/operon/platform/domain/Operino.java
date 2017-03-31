package cloud.operon.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Operino.
 */
@Entity
@Table(name = "operino")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "operino")
public class Operino implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active")
    private Boolean active;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "operino", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OperinoComponent> components = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Operino name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isActive() {
        return active;
    }

    public Operino active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public Operino user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<OperinoComponent> getComponents() {
        return components;
    }

    public Operino components(Set<OperinoComponent> operinoComponents) {
        this.components = operinoComponents;
        return this;
    }

    public Operino addComponents(OperinoComponent operinoComponent) {
        this.components.add(operinoComponent);
        operinoComponent.setOperino(this);
        return this;
    }

    public Operino removeComponents(OperinoComponent operinoComponent) {
        this.components.remove(operinoComponent);
        operinoComponent.setOperino(null);
        return this;
    }

    public void setComponents(Set<OperinoComponent> operinoComponents) {
        this.components = operinoComponents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operino operino = (Operino) o;
        if (operino.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, operino.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Operino{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", active='" + active + "'" +
            '}';
    }
}
