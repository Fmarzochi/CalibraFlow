package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // Ex: "ADMIN", "USER", "AUDITOR"

    // Eu utilizo UUID para garantir a unicidade e segurança dos IDs em ambiente corporativo
}