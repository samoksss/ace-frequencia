package br.unicatolica.ace.frequencia.domain.exception;

/**
 * Violação de uma regra de negócio (RN01..RN11). A camada web traduz para HTTP 422.
 * A mensagem é escrita para a recepção ler, sem jargão técnico.
 */
public class RegraNegocioException extends RuntimeException {

    private final String regra;

    public RegraNegocioException(String regra, String mensagem) {
        super(mensagem);
        this.regra = regra;
    }

    /** Código da regra violada, ex.: "RN01". */
    public String getRegra() {
        return regra;
    }
}
