package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.MatriculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatriculaJpa extends JpaRepository<MatriculaEntity, Long> {

    List<MatriculaEntity> findByTurmaId(Long turmaId);

    List<MatriculaEntity> findByAlunoNumeroMatriculaAndStatus(String numeroMatricula, StatusMatricula status);
}
