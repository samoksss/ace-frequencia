package br.unicatolica.ace.frequencia.application.exception;

/** Cadastro duplicado, ex.: número de matrícula já usado (RN05, HTTP 409). */
public class ConflitoException extends RuntimeException {

    private final String campo;

    public ConflitoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
