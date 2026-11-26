package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.regra.RegraMatricula;
import br.unicatolica.ace.frequencia.domain.regra.SolicitacaoMatricula;
import br.unicatolica.ace.frequencia.domain.vo.Horario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Turma = um horário fixo da semana (ex.: "Segunda 09–11"), com capacidade de 20 alunos.
 * Raiz do agregado que guarda as matrículas.
 * GRASP Information Expert: sabe contar ocupação e avançados.
 * GRASP Creator: é quem cria a Matricula.
 */
public class Turma {

    /** RN01 */
    public static final int CAPACIDADE_MAXIMA = 20;
    /** RN02: só há computadores para 5 alunos de cursos avançados por horário. */
    public static final int LIMITE_AVANCADOS = 5;

    private final Long id;
    private DayOfWeek diaSemana;
    private Horario horario;
    private final List<Matricula> matriculas = new ArrayList<>();

    public Turma(Long id, DayOfWeek diaSemana, Horario horario) {
        this.id = id;
        alterarHorario(diaSemana, horario);
    }

    public final void alterarHorario(DayOfWeek diaSemana, Horario horario) {
        this.diaSemana = Textos.obrigatorio(diaSemana, "diaSemana", "Informe o dia da semana da turma.");
        this.horario = Textos.obrigatorio(horario, "horario", "Informe o horário da turma.");
    }

    /**
     * Cria a matrícula do aluno nesta turma, depois de passar pelo validador de regras (RN01, RN02, RN04, RN06).
     *
     * @param matriculasAtivasDoAluno matrículas ativas que o aluno já tem (em qualquer turma), para a RN04
     */
    public Matricula matricular(Aluno aluno, Curso curso, Ciclo ciclo, LocalDate dataInicio,
                                List<Matricula> matriculasAtivasDoAluno, RegraMatricula validador) {
        Textos.obrigatorio(aluno, "aluno", "Informe o aluno.");
        Textos.obrigatorio(curso, "curso", "Informe o curso.");
        Textos.obrigatorio(ciclo, "ciclo", "Informe o ciclo (cor).");
        validador.validar(new SolicitacaoMatricula(this, aluno, curso, ciclo,
                matriculasAtivasDoAluno == null ? List.of() : matriculasAtivasDoAluno));
        Matricula matricula = new Matricula(null, aluno, this, curso, ciclo, dataInicio);
        matriculas.add(matricula);
        return matricula;
    }

    /** Usado pela persistência para reconstituir o agregado. */
    public void adicionarMatriculaExistente(Matricula matricula) {
        matriculas.add(matricula);
    }

    public List<Matricula> matriculasAtivas() {
        return matriculas.stream().filter(Matricula::isAtiva).toList();
    }

    /** RN03: faltoso continua ocupando a vaga; só a matrícula cancelada libera. */
    public int ocupacao() {
        return matriculasAtivas().size();
    }

    public int avancadosAtivos() {
        return (int) matriculasAtivas().stream().filter(Matricula::isAvancada).count();
    }

    public int vagasDisponiveis() {
        return CAPACIDADE_MAXIMA - ocupacao();
    }

    public boolean isLotada() {
        return ocupacao() >= CAPACIDADE_MAXIMA;
    }

    public boolean isCotaAvancadosCompleta() {
        return avancadosAtivos() >= LIMITE_AVANCADOS;
    }

    public Turno turno() {
        return horario.turno();
    }

    /** Ex.: "Segunda 09–11". */
    public String nome() {
        return DiasDaSemana.nome(diaSemana) + " " + horario.descricao();
    }

    /** Ex.: "14/20 · avançados 3/5". */
    public String resumoOcupacao() {
        return "%d/%d · avançados %d/%d".formatted(ocupacao(), CAPACIDADE_MAXIMA, avancadosAtivos(), LIMITE_AVANCADOS);
    }

    public Long getId() {
        return id;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public Horario getHorario() {
        return horario;
    }

    public List<Matricula> getMatriculas() {
        return Collections.unmodifiableList(matriculas);
    }

    /** Mesma entidade: mesmo id (depois de salva) ou mesma instância (antes de salva). */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Turma outra && id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return nome();
    }
}
