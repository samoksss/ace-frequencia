package br.unicatolica.ace.frequencia.domain.vo;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;

/**
 * Telefone brasileiro com DDD. Guarda só os dígitos (10 ou 11).
 * Sabe se formatar para a tela e gerar o número no formato do link do WhatsApp.
 */
public record Telefone(String digitos) {

    public Telefone {
        String limpo = digitos == null ? "" : digitos.replaceAll("\\D", "");
        if (limpo.startsWith("55") && limpo.length() > 11) {
            limpo = limpo.substring(2);
        }
        if (limpo.length() != 10 && limpo.length() != 11) {
            throw new DadoInvalidoException("telefone", "Telefone inválido: informe DDD + número, ex.: (88) 9 0000-0001.");
        }
        digitos = limpo;
    }

    public static Telefone de(String texto) {
        return new Telefone(texto);
    }

    public String ddd() {
        return digitos.substring(0, 2);
    }

    /** Ex.: (88) 9 0000-0001 ou (88) 3441-0000. */
    public String formatado() {
        String numero = digitos.substring(2);
        if (numero.length() == 9) {
            return "(%s) %s %s-%s".formatted(ddd(), numero.charAt(0), numero.substring(1, 5), numero.substring(5));
        }
        return "(%s) %s-%s".formatted(ddd(), numero.substring(0, 4), numero.substring(4));
    }

    /** Número para https://wa.me/55DDDNUMERO. */
    public String paraWhatsApp() {
        return "55" + digitos;
    }

    @Override
    public String toString() {
        return formatado();
    }
}
