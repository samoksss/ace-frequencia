package br.unicatolica.ace.frequencia.domain.service;

import java.util.List;

/** Critério alternativo: total de faltas no período consultado. */
public class TotalFaltasCriterio implements CriterioAlerta {

    @Override
    public int contar(List<FrequenciaNaAula> historico) {
        return (int) historico.stream().filter(FrequenciaNaAula::isFalta).count();
    }
}
