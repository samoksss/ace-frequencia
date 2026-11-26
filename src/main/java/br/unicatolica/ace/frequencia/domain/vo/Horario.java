package br.unicatolica.ace.frequencia.domain.vo;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.model.Turno;

import java.time.LocalTime;

/**
 * Faixa de horário de uma turma (ex.: 09:00 às 11:00). O turno é derivado da hora de início.
 */
public record Horario(LocalTime inicio, LocalTime fim) {

    public Horario {
        if (inicio == null || fim == null) {
            throw new DadoInvalidoException("horario", "Informe a hora de início e de fim da turma.");
        }
        if (!fim.isAfter(inicio)) {
            throw new DadoInvalidoException("horario", "A hora de fim precisa ser depois da hora de início.");
        }
    }

    public static Horario de(int horaInicio, int horaFim) {
        return new Horario(LocalTime.of(horaInicio, 0), LocalTime.of(horaFim, 0));
    }

    public Turno turno() {
        return Turno.de(inicio);
    }

    /** Ex.: "09–11" (como na planilha) ou "09:30–11:30". */
    public String descricao() {
        return formatar(inicio) + "–" + formatar(fim);
    }

    private static String formatar(LocalTime hora) {
        return hora.getMinute() == 0 ? "%02d".formatted(hora.getHour()) : "%02d:%02d".formatted(hora.getHour(), hora.getMinute());
    }

    @Override
    public String toString() {
        return descricao();
    }
}
