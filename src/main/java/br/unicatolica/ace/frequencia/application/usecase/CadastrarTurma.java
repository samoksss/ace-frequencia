package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.vo.Horario;

import java.time.DayOfWeek;
import java.time.LocalTime;

/** Caso de uso: cadastrar ou alterar uma turma (dia da semana + horário). */
public class CadastrarTurma {

    public record Comando(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
    }

    private final TurmaRepository turmas;

    public CadastrarTurma(TurmaRepository turmas) {
        this.turmas = turmas;
    }

    public Turma cadastrar(Comando c) {
        return turmas.salvar(new Turma(null, c.diaSemana(), new Horario(c.horaInicio(), c.horaFim())));
    }

    public Turma atualizar(Long id, Comando c) {
        Turma turma = turmas.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Turma", id));
        turma.alterarHorario(c.diaSemana(), new Horario(c.horaInicio(), c.horaFim()));
        return turmas.salvar(turma);
    }
}
