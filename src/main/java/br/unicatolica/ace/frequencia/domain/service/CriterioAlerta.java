package br.unicatolica.ace.frequencia.domain.service;

import java.util.List;

/**
 * GoF Strategy: define qual número de faltas decide o nível do alerta.
 * Hoje: faltas consecutivas (RN09). Dá para trocar por "total no período" sem mexer no painel.
 */
public interface CriterioAlerta {

    /** @param historico aulas do aluno, em qualquer ordem */
    int contar(List<FrequenciaNaAula> historico);
}
