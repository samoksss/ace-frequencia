package br.unicatolica.ace.frequencia.domain.service;

import br.unicatolica.ace.frequencia.domain.model.NivelAlerta;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * GRASP Pure Fabrication: concentra o cálculo de faltas, que não pertence naturalmente
 * a nenhuma entidade. O nível do alerta vem do {@link CriterioAlerta} (Strategy).
 */
public class CalculadoraFaltas {

    private final LimitesAlerta limites;
    private final CriterioAlerta criterio;
    private final CriterioAlerta consecutivas = new FaltasConsecutivasCriterio();
    private final CriterioAlerta total = new TotalFaltasCriterio();

    public CalculadoraFaltas(LimitesAlerta limites, CriterioAlerta criterio) {
        this.limites = limites;
        this.criterio = criterio;
    }

    /** RN09 com os limites padrão (2 = ATENÇÃO, 3 = CRÍTICO) e faltas consecutivas. */
    public static CalculadoraFaltas padrao() {
        return new CalculadoraFaltas(LimitesAlerta.PADRAO, new FaltasConsecutivasCriterio());
    }

    public SituacaoFrequencia calcular(List<FrequenciaNaAula> historico) {
        LocalDate ultimaPresenca = historico.stream()
                .filter(aula -> !aula.isFalta())
                .map(FrequenciaNaAula::data)
                .max(Comparator.naturalOrder())
                .orElse(null);
        NivelAlerta nivel = limites.nivelPara(criterio.contar(historico));
        return new SituacaoFrequencia(consecutivas.contar(historico), total.contar(historico), ultimaPresenca, nivel);
    }

    public LimitesAlerta getLimites() {
        return limites;
    }
}
