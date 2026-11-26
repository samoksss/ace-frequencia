package br.unicatolica.ace.frequencia.domain.service;

import java.util.Comparator;
import java.util.List;

/**
 * RN09: conta as faltas seguidas a partir da aula mais recente.
 * Presença (inteira ou parcial, RN08) interrompe a sequência.
 */
public class FaltasConsecutivasCriterio implements CriterioAlerta {

    @Override
    public int contar(List<FrequenciaNaAula> historico) {
        List<FrequenciaNaAula> maisRecentePrimeiro = historico.stream()
                .sorted(Comparator.comparing(FrequenciaNaAula::data).reversed())
                .toList();
        int consecutivas = 0;
        for (FrequenciaNaAula aula : maisRecentePrimeiro) {
            if (!aula.isFalta()) {
                break;
            }
            consecutivas++;
        }
        return consecutivas;
    }
}
