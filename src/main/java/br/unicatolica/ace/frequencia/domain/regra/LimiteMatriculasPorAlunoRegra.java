package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Matricula;

import java.util.List;

/**
 * RN04: o aluno pode ter 1 matrícula ativa. Exceção: curso rápido permite até 2 matrículas
 * ativas, em turmas de dias diferentes (o aluno vem 2 vezes por semana).
 */
public class LimiteMatriculasPorAlunoRegra implements RegraMatricula {

    static final int LIMITE_CURSO_RAPIDO = 2;

    @Override
    public void validar(SolicitacaoMatricula s) {
        List<Matricula> ativas = s.matriculasAtivasDoAluno();
        if (ativas.isEmpty()) {
            return;
        }
        boolean tudoCursoRapido = s.curso().isRapido() && ativas.stream().allMatch(m -> m.getCurso().isRapido());
        if (!tudoCursoRapido) {
            throw new RegraNegocioException("RN04",
                    "%s já tem matrícula ativa na turma %s. Só cursos rápidos permitem uma 2ª matrícula."
                            .formatted(s.aluno().getNome(), ativas.get(0).getTurma().nome()));
        }
        if (ativas.size() >= LIMITE_CURSO_RAPIDO) {
            throw new RegraNegocioException("RN04",
                    "%s já tem %d matrículas ativas de curso rápido (limite).".formatted(s.aluno().getNome(), ativas.size()));
        }
        boolean mesmoDia = ativas.stream().anyMatch(m -> m.getTurma().getDiaSemana() == s.turma().getDiaSemana());
        if (mesmoDia) {
            throw new RegraNegocioException("RN04",
                    "No curso rápido, a 2ª matrícula de %s precisa ser numa turma de outro dia da semana."
                            .formatted(s.aluno().getNome()));
        }
    }
}
