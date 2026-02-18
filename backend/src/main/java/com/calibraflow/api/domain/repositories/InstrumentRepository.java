package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {
    // AQUI MUDOU: O Spring Data busca dentro da entidade Patrimony pelo campo Code
    boolean existsByPatrimonyCode(String code);
}
