package br.unicatolica.ace.frequencia.domain.service;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.model.NivelAlerta;

/**
 * RN09: a partir de quantas faltas o aluno entra em ATENÇÃO e em CRÍTICO.
 * Padrão 2 e 3; configurável em application.yml (frequencia.alerta.*).
 */
public record LimitesAlerta(int atencao, int critico) {

    public static final LimitesAlerta PADRAO = new LimitesAlerta(2, 3);

    public LimitesAlerta {
        if (atencao < 1 || critico <= atencao) {
            throw new DadoInvalidoException("frequencia.alerta",
                    "Limites de alerta inválidos: ATENÇÃO deve ser >= 1 e CRÍTICO maior que ATENÇÃO.");
        }
    }

    public NivelAlerta nivelPara(int faltas) {
        if (faltas >= critico) {
            return NivelAlerta.CRITICO;
        }
        if (faltas >= atencao) {
            return NivelAlerta.ATENCAO;
        }
        return NivelAlerta.NENHUM;
    }
}
