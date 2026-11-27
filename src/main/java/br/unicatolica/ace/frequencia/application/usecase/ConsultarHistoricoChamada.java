package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.AulaRepository;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.Turma;

import java.time.LocalDate;
import java.util.List;

/** Caso de uso: histórico da chamada de uma turma em qualquer período (RN07: nada se perde). */
public class ConsultarHistoricoChamada {

    public record Historico(Turma turma, LocalDate de, LocalDate ate, List<Aula> aulas) {
    }

    private final TurmaRepository turmas;
    private final AulaRepository aulas;

    public ConsultarHistoricoChamada(TurmaRepository turmas, AulaRepository aulas) {
        this.turmas = turmas;
        this.aulas = aulas;
    }

    public Historico executar(Long turmaId, LocalDate de, LocalDate ate) {
        if (de == null || ate == null) {
            throw new DadoInvalidoException("periodo", "Informe o período (de e até).");
        }
        if (ate.isBefore(de)) {
            throw new DadoInvalidoException("periodo", "A data final do período não pode ser antes da inicial.");
        }
        Turma turma = turmas.buscar(turmaId).orElseThrow(() -> new RecursoNaoEncontradoException("Turma", turmaId));
        return new Historico(turma, de, ate, aulas.listar(turma, de, ate));
    }
}
