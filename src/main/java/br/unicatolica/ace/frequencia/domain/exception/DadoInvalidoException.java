package br.unicatolica.ace.frequencia.domain.exception;

/**
 * Dado de entrada inválido para o domínio (formato, campo obrigatório...).
 * A camada web traduz para HTTP 400.
 */
public class DadoInvalidoException extends RuntimeException {

    private final String campo;

    public DadoInvalidoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
