package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.model.Turno;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;

/** Caso de uso: listar turmas (com ocupação) e ver os alunos ativos de uma turma. */
public class ConsultarTurmas {

    private final TurmaRepository turmas;

    public ConsultarTurmas(TurmaRepository turmas) {
        this.turmas = turmas;
    }

    /** Filtros opcionais (null = todos). Ordena por dia da semana e hora de início. */
    public List<Turma> listar(Turno turno, DayOfWeek diaSemana) {
        return turmas.listar().stream()
                .filter(t -> turno == null || t.turno() == turno)
                .filter(t -> diaSemana == null || t.getDiaSemana() == diaSemana)
                .sorted(Comparator.comparing(Turma::getDiaSemana).thenComparing(t -> t.getHorario().inicio()))
                .toList();
    }

    public Turma buscar(Long id) {
        return turmas.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Turma", id));
    }

    /** Alunos ativos da turma, em ordem alfabética. */
    public List<Matricula> matriculasAtivas(Long turmaId) {
        return buscar(turmaId).matriculasAtivas().stream()
                .sorted(Comparator.comparing(m -> m.getAluno().getNome()))
                .toList();
    }
}
