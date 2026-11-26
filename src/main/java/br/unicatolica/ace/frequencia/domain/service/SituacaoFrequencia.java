package br.unicatolica.ace.frequencia.domain.service;

import br.unicatolica.ace.frequencia.domain.model.NivelAlerta;

import java.time.LocalDate;

/**
 * Resultado do cálculo para uma matrícula: o que o Painel de Faltosos exibe.
 *
 * @param ultimaPresenca data da última aula com presença (inteira ou parcial); null se nunca veio
 */
public record SituacaoFrequencia(int faltasConsecutivas, int totalFaltas, LocalDate ultimaPresenca, NivelAlerta nivel) {

    public boolean precisaContato() {
        return nivel != NivelAlerta.NENHUM;
    }
}
