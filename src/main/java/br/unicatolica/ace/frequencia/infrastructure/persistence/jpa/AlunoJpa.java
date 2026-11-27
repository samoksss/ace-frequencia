package br.unicatolica.ace.frequencia.infrastructure.persistence.jpa;

import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunoJpa extends JpaRepository<AlunoEntity, String> {

    List<AlunoEntity> findByNomeContainingIgnoreCaseOrNumeroMatriculaContainingIgnoreCaseOrderByNomeAsc(
            String nome, String numeroMatricula);

    List<AlunoEntity> findAllByOrderByNomeAsc();
}
