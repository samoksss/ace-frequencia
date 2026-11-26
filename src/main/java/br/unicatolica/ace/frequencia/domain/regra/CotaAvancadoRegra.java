package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Turma;

/**
 * RN02: dentro das 20 vagas, no máximo 5 alunos em cursos de categoria AVANCADO,
 * de qualquer tipo (I, II, Kids, Rápido...). Só há computadores para 5 por horário.
 */
public class CotaAvancadoRegra implements RegraMatricula {

    @Override
    public void validar(SolicitacaoMatricula s) {
        Turma turma = s.turma();
        if (s.curso().isAvancado() && turma.isCotaAvancadosCompleta()) {
            throw new RegraNegocioException("RN02",
                    "Turma %s já tem %d alunos de cursos avançados (limite de computadores)."
                            .formatted(turma.nome(), turma.avancadosAtivos()));
        }
    }
}
