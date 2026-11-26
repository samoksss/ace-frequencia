package br.unicatolica.ace.frequencia.domain.vo;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;

/**
 * Número de matrícula já usado pela escola (ex.: "9305", "7102J").
 * É texto, não número: pode ter letra no final. Único por aluno (RN05).
 */
public record NumeroMatricula(String valor) {

    private static final String FORMATO = "[0-9A-Z]{1,12}";

    public NumeroMatricula {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("numeroMatricula", "Informe o número de matrícula do aluno.");
        }
        valor = valor.trim().toUpperCase();
        if (!valor.matches(FORMATO)) {
            throw new DadoInvalidoException("numeroMatricula",
                    "Número de matrícula inválido: use só números e letras (ex.: 9305 ou 7102J).");
        }
    }

    public static NumeroMatricula de(String valor) {
        return new NumeroMatricula(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
