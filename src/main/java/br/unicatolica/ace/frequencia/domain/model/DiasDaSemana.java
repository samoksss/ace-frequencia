package br.unicatolica.ace.frequencia.domain.model;

import java.time.DayOfWeek;

/** Nomes dos dias como a escola usa na planilha. */
public final class DiasDaSemana {

    private DiasDaSemana() {
    }

    public static String nome(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }
}
