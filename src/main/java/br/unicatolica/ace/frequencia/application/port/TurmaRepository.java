package br.unicatolica.ace.frequencia.application.port;

import br.unicatolica.ace.frequencia.domain.model.Turma;

import java.util.List;
import java.util.Optional;

/** Porta do agregado Turma: sempre devolve a turma com todas as suas matrículas. */
public interface TurmaRepository {

    Optional<Turma> buscar(Long id);

    List<Turma> listar();

    /** Salva os dados da turma (dia e horário). Matrículas são salvas pelo MatriculaRepository. */
    Turma salvar(Turma turma);
}
