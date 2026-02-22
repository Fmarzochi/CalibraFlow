package com.calibraflow.api.domain.entities;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.OffsetDateTime;

@Entity
@Table(name = "instrument_status_history")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class InstrumentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private InstrumentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private InstrumentStatus newStatus;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private OffsetDateTime changedAt;

    @Column(name = "responsible_id", nullable = false)
    private Long responsibleId;

    @Column(name = "responsible_full_name", nullable = false)
    private String responsibleFullName;

    @Column(name = "responsible_cpf", nullable = false)
    private String responsibleCpf;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justification;

    @PrePersist
    protected void onCreate() {
        this.changedAt = OffsetDateTime.now();
    }
}