package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.RegistroFrequenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroFrequenciaJpa extends JpaRepository<RegistroFrequenciaEntity, Long> {

    List<RegistroFrequenciaEntity> findByAulaId(Long aulaId);
}
