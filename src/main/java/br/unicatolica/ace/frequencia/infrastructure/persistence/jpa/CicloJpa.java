package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.CicloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CicloJpa extends JpaRepository<CicloEntity, Long> {

    List<CicloEntity> findAllByOrderByDataInicioAsc();
}
