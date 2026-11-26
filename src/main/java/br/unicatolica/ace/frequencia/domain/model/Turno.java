package br.unicatolica.ace.frequencia.domain.model;

import java.time.LocalTime;

/** Turno derivado da hora de início da turma. */
public enum Turno {
    MANHA, TARDE, NOITE;

    /** MANHA: antes de 12h · TARDE: 12h–17h59 · NOITE: 18h em diante. */
    public static Turno de(LocalTime inicio) {
        if (inicio.isBefore(LocalTime.NOON)) {
            return MANHA;
        }
        if (inicio.isBefore(LocalTime.of(18, 0))) {
            return TARDE;
        }
        return NOITE;
    }
}
