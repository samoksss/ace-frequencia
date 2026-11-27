package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoJpa extends JpaRepository<CursoEntity, Long> {

    List<CursoEntity> findAllByOrderByNomeAsc();
}
