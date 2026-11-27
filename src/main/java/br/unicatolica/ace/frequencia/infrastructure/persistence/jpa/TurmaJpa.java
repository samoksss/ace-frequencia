package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.TurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaJpa extends JpaRepository<TurmaEntity, Long> {
}
