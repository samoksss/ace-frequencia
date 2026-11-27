package br.unicatolica.ace.frequencia.web.dto;

import br.unicatolica.ace.frequencia.domain.model.Ciclo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public final class CicloDto {

    private CicloDto() {
    }

    public record Requisicao(
            @Schema(example = "Azul") @NotBlank(message = "Informe o nome do ciclo.") String nome,
            @Schema(example = "#1E88E5") @NotBlank(message = "Informe a cor (#RRGGBB).") String corHex,
            @Schema(example = "2026-08-03") @NotNull(message = "Informe a data de início.") LocalDate dataInicio,
            @Schema(example = "2027-07-31") @NotNull(message = "Informe a previsão de término.") LocalDate dataPrevistaTermino,
            @Schema(example = "true") @NotNull(message = "Informe se o ciclo aceita matrículas.") Boolean aceitaMatriculas) {
    }

    public record Resposta(Long id, String nome, String corHex, LocalDate dataInicio, LocalDate dataPrevistaTermino,
                           boolean aceitaMatriculas) {

        public static Resposta de(Ciclo c) {
            return new Resposta(c.getId(), c.getNome(), c.getCorHex(), c.getDataInicio(), c.getDataPrevistaTermino(),
                    c.isAceitaMatriculas());
        }
    }
}
