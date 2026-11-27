package br.unicatolica.ace.frequencia.web.dto;

import br.unicatolica.ace.frequencia.domain.model.CategoriaCurso;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public final class MatriculaDto {

    private MatriculaDto() {
    }

    public record Requisicao(
            @Schema(example = "9305") @NotBlank(message = "Informe o número de matrícula do aluno.") String alunoNumero,
            @Schema(example = "1") @NotNull(message = "Informe a turma.") Long turmaId,
            @Schema(example = "1") @NotNull(message = "Informe o curso.") Long cursoId,
            @Schema(example = "1") @NotNull(message = "Informe o ciclo (cor).") Long cicloId,
            @Schema(description = "Opcional; se vazio, usa a data de hoje", example = "2026-11-27") LocalDate dataInicio) {
    }

    public record Cancelamento(
            @Schema(example = "Mudou de cidade") @NotBlank(message = "Informe o motivo do cancelamento.") String motivo) {
    }

    public record Resposta(Long id, String alunoNumero, String alunoNome, String telefone, String whatsapp,
                           Long turmaId, String turmaNome, Long cursoId, String cursoNome, CategoriaCurso categoria,
                           Long cicloId, String cicloNome, String cicloCor, StatusMatricula status,
                           LocalDate dataInicio, LocalDate dataPrevistaTermino, String motivoCancelamento,
                           LocalDate dataCancelamento) {

        public static Resposta de(Matricula m) {
            return new Resposta(m.getId(), m.getAluno().getNumeroMatricula().valor(), m.getAluno().getNome(),
                    m.getAluno().getTelefone().formatado(), m.getAluno().getTelefone().paraWhatsApp(),
                    m.getTurma().getId(), m.getTurma().nome(), m.getCurso().getId(), m.getCurso().getNome(),
                    m.getCurso().getCategoria(), m.getCiclo().getId(), m.getCiclo().getNome(), m.getCiclo().getCorHex(),
                    m.getStatus(), m.getDataInicio(), m.getDataPrevistaTermino(), m.getMotivoCancelamento(),
                    m.getDataCancelamento());
        }
    }
}
