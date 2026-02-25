package com.calibraflow.api.domain.entities;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "instrument_status_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class InstrumentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private InstrumentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstrumentStatus status;

    @Column(name = "responsible_id")
    private Long responsibleId;

    @Column(name = "responsible_full_name")
    private String responsibleFullName;

    @Column(name = "responsible_cpf")
    private String responsibleCpf;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;
}