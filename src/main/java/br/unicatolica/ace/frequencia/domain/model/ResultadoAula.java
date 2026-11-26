package br.unicatolica.ace.frequencia.domain.model;

/**
 * Resultado de uma aula de 2 horas para o aluno (RN08).
 * FALTA só quando ausente nas duas horas; presente em só uma hora = PARCIAL (não conta como falta).
 */
public enum ResultadoAula {
    PRESENTE, PARCIAL, FALTA;

    public static ResultadoAula de(Presenca primeiraHora, Presenca segundaHora) {
        boolean faltou1 = primeiraHora == Presenca.FALTA;
        boolean faltou2 = segundaHora == Presenca.FALTA;
        if (faltou1 && faltou2) {
            return FALTA;
        }
        if (faltou1 || faltou2) {
            return PARCIAL;
        }
        return PRESENTE;
    }
}
