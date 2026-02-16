package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório para gestão de movimentações de instrumentos.
 * Responsável por persistir e consultar o histórico de auditoria.
 */
@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {

    /**
     * Busca todas as movimentações de um instrumento específico.
     * @param instrumentId O UUID do instrumento para rastreio.
     * @return Uma lista de movimentações ordenadas da mais recente para a mais antiga.
     */
    List<Movement> findByInstrumentIdOrderByMovementDateDesc(UUID instrumentId);
}