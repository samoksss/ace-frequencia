package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;

/** RN06: ciclo marcado como "não aceita matrículas" (ex.: Laranja, encerrando) bloqueia novas matrículas. */
public class CicloAceitaMatriculaRegra implements RegraMatricula {

    @Override
    public void validar(SolicitacaoMatricula s) {
        if (!s.ciclo().isAceitaMatriculas()) {
            throw new RegraNegocioException("RN06",
                    "O ciclo %s está encerrando e não aceita novas matrículas.".formatted(s.ciclo().getNome()));
        }
    }
}
