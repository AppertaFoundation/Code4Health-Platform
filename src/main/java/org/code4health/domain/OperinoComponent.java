package org.code4health.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.code4health.domain.enumeration.HostingType;
import org.code4health.domain.enumeration.OperinoComponentType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A OperinoComponent.
 */
@Entity
@Table(name = "operino_component")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "operinocomponent")
public class OperinoComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "hosting", nullable = false)
    private HostingType hosting;

    @Column(name = "availability")
    private Boolean availability;

    @Column(name = "apply_limits")
    private Boolean applyLimits;

    @Column(name = "records_number")
    private Long recordsNumber;

    @Column(name = "transactions_limit")
    private Long transactionsLimit;

    @Column(name = "disk_space")
    private Long diskSpace;

    @Column(name = "compute_resource_limit")
    private Long computeResourceLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OperinoComponentType type;

    @ManyToOne
    @JsonIgnore
    private Operino operino;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HostingType getHosting() {
        return hosting;
    }

    public OperinoComponent hosting(HostingType hosting) {
        this.hosting = hosting;
        return this;
    }

    public void setHosting(HostingType hosting) {
        this.hosting = hosting;
    }

    public Boolean isAvailability() {
        return availability;
    }

    public OperinoComponent availability(Boolean availability) {
        this.availability = availability;
        return this;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Boolean isApplyLimits() {
        return applyLimits;
    }

    public OperinoComponent applyLimits(Boolean applyLimits) {
        this.applyLimits = applyLimits;
        return this;
    }

    public void setApplyLimits(Boolean applyLimits) {
        this.applyLimits = applyLimits;
    }

    public Long getRecordsNumber() {
        return recordsNumber;
    }

    public OperinoComponent recordsNumber(Long recordsNumber) {
        this.recordsNumber = recordsNumber;
        return this;
    }

    public void setRecordsNumber(Long recordsNumber) {
        this.recordsNumber = recordsNumber;
    }

    public Long getTransactionsLimit() {
        return transactionsLimit;
    }

    public OperinoComponent transactionsLimit(Long transactionsLimit) {
        this.transactionsLimit = transactionsLimit;
        return this;
    }

    public void setTransactionsLimit(Long transactionsLimit) {
        this.transactionsLimit = transactionsLimit;
    }

    public Long getDiskSpace() {
        return diskSpace;
    }

    public OperinoComponent diskSpace(Long diskSpace) {
        this.diskSpace = diskSpace;
        return this;
    }

    public void setDiskSpace(Long diskSpace) {
        this.diskSpace = diskSpace;
    }

    public Long getComputeResourceLimit() {
        return computeResourceLimit;
    }

    public OperinoComponent computeResourceLimit(Long computeResourceLimit) {
        this.computeResourceLimit = computeResourceLimit;
        return this;
    }

    public void setComputeResourceLimit(Long computeResourceLimit) {
        this.computeResourceLimit = computeResourceLimit;
    }

    public OperinoComponentType getType() {
        return type;
    }

    public OperinoComponent type(OperinoComponentType type) {
        this.type = type;
        return this;
    }

    public void setType(OperinoComponentType type) {
        this.type = type;
    }

    public Operino getOperino() {
        return operino;
    }

    public OperinoComponent operino(Operino operino) {
        this.operino = operino;
        return this;
    }

    public void setOperino(Operino operino) {
        this.operino = operino;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperinoComponent operinoComponent = (OperinoComponent) o;
        if (operinoComponent.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, operinoComponent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OperinoComponent{" +
            "id=" + id +
            ", hosting='" + hosting + "'" +
            ", availability='" + availability + "'" +
            ", applyLimits='" + applyLimits + "'" +
            ", recordsNumber='" + recordsNumber + "'" +
            ", transactionsLimit='" + transactionsLimit + "'" +
            ", diskSpace='" + diskSpace + "'" +
            ", computeResourceLimit='" + computeResourceLimit + "'" +
            ", type='" + type + "'" +
            '}';
    }
}
