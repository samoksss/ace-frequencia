package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.AulaRepository;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Presenca;
import br.unicatolica.ace.frequencia.domain.model.RegistroFrequencia;
import br.unicatolica.ace.frequencia.domain.model.Turma;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Casos de uso da chamada digital (RN07, RN08):
 * abrir a chamada de uma turma numa data (já lançada ou nova) e salvar/corrigir as marcações.
 */
public class RegistrarChamada {

    public record Marcacao(Long matriculaId, Presenca primeiraHora, Presenca segundaHora) {
    }

    private final TurmaRepository turmas;
    private final AulaRepository aulas;
    private final Clock relogio;

    public RegistrarChamada(TurmaRepository turmas, AulaRepository aulas, Clock relogio) {
        this.turmas = turmas;
        this.aulas = aulas;
        this.relogio = relogio;
    }

    /**
     * Chamada da data: se já foi lançada, mostra o que foi salvo (incluindo alunos matriculados depois);
     * se não, devolve uma chamada nova pré-preenchida com os alunos ativos (ainda não salva).
     */
    public Aula abrir(Long turmaId, LocalDate data) {
        Turma turma = turma(turmaId);
        return aulas.buscar(turma, data)
                .map(aula -> {
                    aula.incluirMatriculasAtivasSemRegistro();
                    return aula;
                })
                .orElseGet(() -> Aula.abrir(turma, data, LocalDate.now(relogio)));
    }

    /** Salva ou corrige a chamada. Alunos não enviados ficam com a marcação atual. */
    public Aula salvar(Long turmaId, LocalDate data, List<Marcacao> marcacoes) {
        Aula aula = abrir(turmaId, data);
        LocalDateTime agora = LocalDateTime.now(relogio);
        for (Marcacao m : marcacoes) {
            aula.registrar(matriculaDaTurma(aula.getTurma(), m.matriculaId()), m.primeiraHora(), m.segundaHora(), agora);
        }
        for (RegistroFrequencia r : aula.getRegistros()) {
            if (r.getAtualizadoEm() == null) {
                r.registrar(r.getPrimeiraHora(), r.getSegundaHora(), agora);
            }
        }
        return aulas.salvar(aula);
    }

    private Turma turma(Long turmaId) {
        return turmas.buscar(turmaId).orElseThrow(() -> new RecursoNaoEncontradoException("Turma", turmaId));
    }

    private static Matricula matriculaDaTurma(Turma turma, Long matriculaId) {
        return turma.getMatriculas().stream()
                .filter(m -> m.getId() != null && m.getId().equals(matriculaId))
                .findFirst()
                .orElseThrow(() -> new DadoInvalidoException("matriculaId",
                        "A matrícula " + matriculaId + " não é da turma " + turma.nome() + "."));
    }
}
