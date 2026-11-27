package br.unicatolica.ace.frequencia.infrastructure.persistence.entity;

import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "matricula")
public class MatriculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aluno_numero", nullable = false)
    private AlunoEntity aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private TurmaEntity turma;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private CursoEntity curso;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private CicloEntity ciclo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMatricula status;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    public MatriculaEntity() {
    }

    public void preencher(AlunoEntity aluno, TurmaEntity turma, CursoEntity curso, CicloEntity ciclo,
                          LocalDate dataInicio, StatusMatricula status, String motivoCancelamento,
                          LocalDate dataCancelamento) {
        this.aluno = aluno;
        this.turma = turma;
        this.curso = curso;
        this.ciclo = ciclo;
        this.dataInicio = dataInicio;
        this.status = status;
        this.motivoCancelamento = motivoCancelamento;
        this.dataCancelamento = dataCancelamento;
    }

    public Long getId() {
        return id;
    }

    public AlunoEntity getAluno() {
        return aluno;
    }

    public TurmaEntity getTurma() {
        return turma;
    }

    public CursoEntity getCurso() {
        return curso;
    }

    public CicloEntity getCiclo() {
        return ciclo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
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
}
