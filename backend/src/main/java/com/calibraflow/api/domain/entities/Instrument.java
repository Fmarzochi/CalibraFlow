package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_instruments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instrument {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String serialNumber;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patrimony_id")
    private Patrimony patrimony;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.active == null) this.active = true;
        if (this.deleted == null) this.deleted = false;
    }
}