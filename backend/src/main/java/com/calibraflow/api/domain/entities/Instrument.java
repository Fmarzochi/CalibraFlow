package com.calibraflow.api.domain.entities;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instruments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, unique = true)
    private String tag;

    @Column(nullable = false)
    private String name;

    @Column(name = "serial_number")
    private String serialNumber;

    private String manufacturer;

    private String model;

    private String location;

    private String tolerance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstrumentStatus status = InstrumentStatus.ATIVO;

}