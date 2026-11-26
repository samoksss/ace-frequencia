package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;

import java.time.LocalDate;

/**
 * Vínculo Aluno ↔ Turma, com Curso e Ciclo. Só matrículas ATIVAS ocupam vaga.
 * Criada apenas pela Turma (GRASP Creator), depois de validada pelas regras.
 * GRASP Information Expert: sabe se está ativa e se é de curso avançado.
 */
public class Matricula {

    private final Long id;
    private final Aluno aluno;
    private final Turma turma;
    private final Curso curso;
    private final Ciclo ciclo;
    private final LocalDate dataInicio;
    private final LocalDate dataPrevistaTermino;
    private StatusMatricula status;
    private String motivoCancelamento;
    private LocalDate dataCancelamento;

    Matricula(Long id, Aluno aluno, Turma turma, Curso curso, Ciclo ciclo, LocalDate dataInicio) {
        this(id, aluno, turma, curso, ciclo, dataInicio, StatusMatricula.ATIVA, null, null);
    }

    /** Construtor completo, usado também para reconstituir a partir da persistência. */
    public Matricula(Long id, Aluno aluno, Turma turma, Curso curso, Ciclo ciclo, LocalDate dataInicio,
                     StatusMatricula status, String motivoCancelamento, LocalDate dataCancelamento) {
        this.id = id;
        this.aluno = Textos.obrigatorio(aluno, "aluno", "Informe o aluno.");
        this.turma = Textos.obrigatorio(turma, "turma", "Informe a turma.");
        this.curso = Textos.obrigatorio(curso, "curso", "Informe o curso.");
        this.ciclo = Textos.obrigatorio(ciclo, "ciclo", "Informe o ciclo (cor).");
        this.dataInicio = Textos.obrigatorio(dataInicio, "dataInicio", "Informe a data de início da matrícula.");
        this.dataPrevistaTermino = dataInicio.plusMonths(curso.duracaoMeses()); // RN11
        this.status = Textos.obrigatorio(status, "status", "Informe o status da matrícula.");
        this.motivoCancelamento = motivoCancelamento;
        this.dataCancelamento = dataCancelamento;
    }

    /**
     * RN10: cancelar exige motivo. A vaga é liberada (RN03), mas todo o histórico de frequência é mantido.
     */
    public void cancelar(String motivo, LocalDate data) {
        if (!isAtiva()) {
            throw new RegraNegocioException("RN10", "Esta matrícula já está " + status.name().toLowerCase() + ".");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException("RN10", "Informe o motivo do cancelamento.");
        }
        this.status = StatusMatricula.CANCELADA;
        this.motivoCancelamento = motivo.trim();
        this.dataCancelamento = Textos.obrigatorio(data, "data", "Informe a data do cancelamento.");
    }

    public boolean isAtiva() {
        return status == StatusMatricula.ATIVA;
    }

    public boolean isAvancada() {
        return curso.isAvancado();
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public Curso getCurso() {
        return curso;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public LocalDate getDataCancelamento() {
        return dataCancelamento;
    }

    /** Mesma entidade: mesmo id (depois de salva) ou mesma instância (antes de salva). */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Matricula outra && id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return aluno + " em " + turma.nome() + " [" + status + "]";
    }
}
