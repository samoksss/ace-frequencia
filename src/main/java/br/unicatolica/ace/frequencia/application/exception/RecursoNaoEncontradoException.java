package br.unicatolica.ace.frequencia.application.exception;

/** Recurso pedido não existe (HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Object identificador) {
        super("%s não encontrado(a): %s.".formatted(recurso, identificador));
    }
}
