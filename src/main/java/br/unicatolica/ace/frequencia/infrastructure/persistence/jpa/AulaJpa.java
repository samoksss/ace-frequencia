package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.AulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AulaJpa extends JpaRepository<AulaEntity, Long> {

    Optional<AulaEntity> findByTurmaIdAndData(Long turmaId, LocalDate data);

    List<AulaEntity> findByTurmaIdAndDataBetweenOrderByDataAsc(Long turmaId, LocalDate de, LocalDate ate);
}
