package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;

/** Validações simples de texto usadas pelas entidades. */
final class Textos {

    private Textos() {
    }

    static String obrigatorio(String valor, String campo, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException(campo, mensagem);
        }
        return valor.trim();
    }

    static String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    static <T> T obrigatorio(T valor, String campo, String mensagem) {
        if (valor == null) {
            throw new DadoInvalidoException(campo, mensagem);
        }
        return valor;
    }
}
