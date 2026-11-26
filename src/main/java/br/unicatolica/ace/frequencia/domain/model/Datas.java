package br.unicatolica.ace.frequencia.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Formatação de datas no padrão brasileiro para mensagens. */
public final class Datas {

    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Datas() {
    }

    public static String formatar(LocalDate data) {
        return data.format(BR);
    }
}
