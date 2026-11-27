package br.unicatolica.ace.frequencia.web.dto;

import br.unicatolica.ace.frequencia.domain.model.DiasDaSemana;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.model.Turno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public final class TurmaDto {

    private TurmaDto() {
    }

    public record Requisicao(
            @Schema(example = "MONDAY") @NotNull(message = "Informe o dia da semana.") DayOfWeek diaSemana,
            @Schema(type = "string", example = "09:00") @NotNull(message = "Informe a hora de início.") LocalTime horaInicio,
            @Schema(type = "string", example = "11:00") @NotNull(message = "Informe a hora de fim.") LocalTime horaFim) {
    }

    /** Inclui a ocupação para a tela: ex. "14/20 · avançados 3/5". */
    public record Resposta(Long id, String nome, DayOfWeek diaSemana, String diaSemanaNome, LocalTime horaInicio,
                           LocalTime horaFim, Turno turno, int ocupacao, int capacidade, int avancados,
                           int limiteAvancados, int vagas, boolean lotada, String resumoOcupacao) {

        public static Resposta de(Turma t) {
            return new Resposta(t.getId(), t.nome(), t.getDiaSemana(), DiasDaSemana.nome(t.getDiaSemana()),
                    t.getHorario().inicio(), t.getHorario().fim(), t.turno(), t.ocupacao(), Turma.CAPACIDADE_MAXIMA,
                    t.avancadosAtivos(), Turma.LIMITE_AVANCADOS, t.vagasDisponiveis(), t.isLotada(), t.resumoOcupacao());
        }
    }
}
