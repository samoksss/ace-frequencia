package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.MatriculaRepository;
import br.unicatolica.ace.frequencia.domain.model.Matricula;

import java.time.Clock;
import java.time.LocalDate;

/** Caso de uso: cancelar matrícula com motivo (RN10). Libera a vaga (RN03) e mantém o histórico. */
public class CancelarMatricula {

    private final MatriculaRepository matriculas;
    private final Clock relogio;

    public CancelarMatricula(MatriculaRepository matriculas, Clock relogio) {
        this.matriculas = matriculas;
        this.relogio = relogio;
    }

    public Matricula executar(Long matriculaId, String motivo) {
        Matricula matricula = matriculas.buscar(matriculaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula", matriculaId));
        matricula.cancelar(motivo, LocalDate.now(relogio));
        return matriculas.salvar(matricula);
    }
}
