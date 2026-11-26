package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.model.Ciclo;
import br.unicatolica.ace.frequencia.domain.model.Curso;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;

import java.util.List;

/**
 * Tudo o que as regras precisam para decidir se a matrícula pode ser feita.
 *
 * @param matriculasAtivasDoAluno matrículas que o aluno já tem, em qualquer turma
 */
public record SolicitacaoMatricula(Turma turma, Aluno aluno, Curso curso, Ciclo ciclo,
                                   List<Matricula> matriculasAtivasDoAluno) {

    public SolicitacaoMatricula {
        matriculasAtivasDoAluno = matriculasAtivasDoAluno.stream().filter(Matricula::isAtiva).toList();
    }
}
