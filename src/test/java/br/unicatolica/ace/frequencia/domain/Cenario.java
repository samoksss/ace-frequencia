package br.unicatolica.ace.frequencia.domain;

import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.model.CategoriaCurso;
import br.unicatolica.ace.frequencia.domain.model.Ciclo;
import br.unicatolica.ace.frequencia.domain.model.Curso;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.regra.ValidadorMatricula;
import br.unicatolica.ace.frequencia.domain.vo.Horario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Dados fictícios reutilizados pelos testes de domínio. Nenhum dado real da New Center. */
public final class Cenario {

    public static final LocalDate HOJE = LocalDate.of(2026, 11, 26);

    public static final Curso BASICO = new Curso(1L, "Básico", CategoriaCurso.BASICO, false);
    public static final Curso BASICO_RAPIDO = new Curso(2L, "Básico Rápido", CategoriaCurso.BASICO, true);
    public static final Curso AVANCADO_I = new Curso(3L, "Avançado I – Empresarial", CategoriaCurso.AVANCADO, false);
    public static final Curso AVANCADO_II = new Curso(4L, "Avançado II – Design Gráfico", CategoriaCurso.AVANCADO, false);
    public static final Curso AVANCADO_KIDS = new Curso(5L, "Avançado Kids", CategoriaCurso.AVANCADO, false);
    public static final Curso AVANCADO_RAPIDO = new Curso(6L, "Avançado Rápido", CategoriaCurso.AVANCADO, true);

    private static final AtomicInteger SEQUENCIA = new AtomicInteger(1);

    private Cenario() {
    }

    public static Ciclo azul() {
        return new Ciclo(1L, "Azul", "#1E88E5", LocalDate.of(2026, 8, 1), LocalDate.of(2027, 7, 31), true);
    }

    public static Ciclo laranja() {
        return new Ciclo(2L, "Laranja", "#FB8C00", LocalDate.of(2025, 12, 1), LocalDate.of(2026, 12, 20), false);
    }

    private static final AtomicInteger IDS_TURMA = new AtomicInteger(1);

    public static Turma turma(DayOfWeek dia, int horaInicio, int horaFim) {
        return new Turma((long) IDS_TURMA.getAndIncrement(), dia, Horario.de(horaInicio, horaFim));
    }

    public static Turma segunda09() {
        return turma(DayOfWeek.MONDAY, 9, 11);
    }

    public static Aluno novoAluno() {
        int n = SEQUENCIA.getAndIncrement();
        return Aluno.novo(String.valueOf(9000 + n), "Aluno Fictício " + n, "(88) 9 0000-%04d".formatted(n % 10000));
    }

    public static Matricula matricular(Turma turma, Curso curso) {
        return turma.matricular(novoAluno(), curso, azul(), HOJE, List.of(), ValidadorMatricula.padrao());
    }

    /** Preenche a turma com {@code quantidade} alunos do curso informado. */
    public static void preencher(Turma turma, Curso curso, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            matricular(turma, curso);
        }
    }
}
