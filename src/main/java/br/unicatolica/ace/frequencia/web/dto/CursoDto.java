package br.unicatolica.ace.frequencia.web.dto;

import br.unicatolica.ace.frequencia.domain.model.CategoriaCurso;
import br.unicatolica.ace.frequencia.domain.model.Curso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class CursoDto {

    private CursoDto() {
    }

    public record Requisicao(
            @Schema(example = "Avançado Kids") @NotBlank(message = "Informe o nome do curso.") String nome,
            @Schema(example = "AVANCADO") @NotNull(message = "Informe a categoria (BASICO ou AVANCADO).") CategoriaCurso categoria,
            @Schema(example = "false") boolean rapido) {
    }

    public record Resposta(Long id, String nome, CategoriaCurso categoria, boolean rapido, int duracaoMeses) {

        public static Resposta de(Curso c) {
            return new Resposta(c.getId(), c.getNome(), c.getCategoria(), c.isRapido(), c.duracaoMeses());
        }
    }
}
