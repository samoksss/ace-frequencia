package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.AlunoRepository;
import br.unicatolica.ace.frequencia.application.port.CicloRepository;
import br.unicatolica.ace.frequencia.application.port.CursoRepository;
import br.unicatolica.ace.frequencia.application.port.MatriculaRepository;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.model.Ciclo;
import br.unicatolica.ace.frequencia.domain.model.Curso;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.regra.RegraMatricula;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso: matricular aluno numa turma (GRASP Controller).
 * Só coordena: busca os dados, pede à Turma para criar a matrícula (as regras ficam no domínio) e salva.
 */
public class MatricularAluno {

    /** @param dataInicio opcional; se nula, usa a data de hoje */
    public record Comando(String alunoNumero, Long turmaId, Long cursoId, Long cicloId, LocalDate dataInicio) {
    }

    private final AlunoRepository alunos;
    private final TurmaRepository turmas;
    private final CursoRepository cursos;
    private final CicloRepository ciclos;
    private final MatriculaRepository matriculas;
    private final RegraMatricula validador;
    private final Clock relogio;

    public MatricularAluno(AlunoRepository alunos, TurmaRepository turmas, CursoRepository cursos,
                           CicloRepository ciclos, MatriculaRepository matriculas, RegraMatricula validador,
                           Clock relogio) {
        this.alunos = alunos;
        this.turmas = turmas;
        this.cursos = cursos;
        this.ciclos = ciclos;
        this.matriculas = matriculas;
        this.validador = validador;
        this.relogio = relogio;
    }

    public Matricula executar(Comando c) {
        NumeroMatricula numero = NumeroMatricula.de(c.alunoNumero());
        Aluno aluno = alunos.buscar(numero).orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", numero));
        Turma turma = turmas.buscar(c.turmaId()).orElseThrow(() -> new RecursoNaoEncontradoException("Turma", c.turmaId()));
        Curso curso = cursos.buscar(c.cursoId()).orElseThrow(() -> new RecursoNaoEncontradoException("Curso", c.cursoId()));
        Ciclo ciclo = ciclos.buscar(c.cicloId()).orElseThrow(() -> new RecursoNaoEncontradoException("Ciclo", c.cicloId()));
        List<Matricula> ativasDoAluno = matriculas.ativasDoAluno(numero);
        LocalDate inicio = c.dataInicio() != null ? c.dataInicio() : LocalDate.now(relogio);

        Matricula nova = turma.matricular(aluno, curso, ciclo, inicio, ativasDoAluno, validador);
        return matriculas.salvar(nova);
    }
}
