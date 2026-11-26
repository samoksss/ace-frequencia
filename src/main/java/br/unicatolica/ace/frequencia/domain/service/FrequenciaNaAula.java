package br.unicatolica.ace.frequencia.domain.service;

import br.unicatolica.ace.frequencia.domain.model.Presenca;
import br.unicatolica.ace.frequencia.domain.model.RegistroFrequencia;
import br.unicatolica.ace.frequencia.domain.model.ResultadoAula;

import java.time.LocalDate;

/** O resultado de um aluno numa data de aula: entrada para o cálculo de faltas. */
public record FrequenciaNaAula(LocalDate data, ResultadoAula resultado) {

    public static FrequenciaNaAula de(LocalDate data, Presenca primeiraHora, Presenca segundaHora) {
        return new FrequenciaNaAula(data, ResultadoAula.de(primeiraHora, segundaHora));
    }

    public static FrequenciaNaAula de(LocalDate data, RegistroFrequencia registro) {
        return new FrequenciaNaAula(data, registro.resultado());
    }

    public boolean isFalta() {
        return resultado == ResultadoAula.FALTA;
    }
}
