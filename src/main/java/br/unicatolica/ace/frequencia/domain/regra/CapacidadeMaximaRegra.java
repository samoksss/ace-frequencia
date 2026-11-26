package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Turma;

/**
 * RN01: cada turma tem no máximo 20 matrículas ATIVAS.
 * RN03: faltoso continua ocupando a vaga; não existe "substituir quem faltou".
 */
public class CapacidadeMaximaRegra implements RegraMatricula {

    @Override
    public void validar(SolicitacaoMatricula s) {
        Turma turma = s.turma();
        if (turma.isLotada()) {
            throw new RegraNegocioException("RN01",
                    "Turma %s já está lotada (%d/%d alunos ativos). Alunos faltosos continuam ocupando a vaga; "
                            .formatted(turma.nome(), turma.ocupacao(), Turma.CAPACIDADE_MAXIMA)
                            + "para liberar, é preciso cancelar uma matrícula.");
        }
    }
}
