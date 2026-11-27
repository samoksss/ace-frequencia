package br.unicatolica.ace.frequencia.web.dto;

import br.unicatolica.ace.frequencia.domain.model.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public final class AlunoDto {

    private AlunoDto() {
    }

    public record Requisicao(
            @Schema(example = "9305") @NotBlank(message = "Informe o número de matrícula.") String numeroMatricula,
            @Schema(example = "Ana Fictícia Souza") @NotBlank(message = "Informe o nome completo.") String nome,
            @Schema(example = "(88) 9 0000-0001") @NotBlank(message = "Informe o telefone.") String telefone,
            @Schema(example = "Prefere contato à tarde") String observacao) {
    }

    /** O número de matrícula não muda; vem na URL. */
    public record Atualizacao(
            @Schema(example = "Ana Fictícia Souza") @NotBlank(message = "Informe o nome completo.") String nome,
            @Schema(example = "(88) 9 0000-0001") @NotBlank(message = "Informe o telefone.") String telefone,
            String observacao) {
    }

    public record Resposta(String numeroMatricula, String nome, String telefone, String whatsapp, String observacao) {

        public static Resposta de(Aluno a) {
            return new Resposta(a.getNumeroMatricula().valor(), a.getNome(), a.getTelefone().formatado(),
                    a.getTelefone().paraWhatsApp(), a.getObservacao());
        }
    }
}
