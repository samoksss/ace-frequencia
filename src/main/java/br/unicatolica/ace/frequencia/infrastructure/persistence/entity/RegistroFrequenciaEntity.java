package br.unicatolica.ace.frequencia.infrastructure.persistence.entity;

import br.unicatolica.ace.frequencia.domain.model.Presenca;
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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/** Frequência de uma matrícula numa aula. Nunca é apagado, só corrigido (RN07). */
@Entity
@Table(name = "registro_frequencia",
        uniqueConstraints = @UniqueConstraint(name = "uk_registro_aula_matricula", columnNames = {"aula_id", "matricula_id"}))
public class RegistroFrequenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private AulaEntity aula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private MatriculaEntity matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "primeira_hora", nullable = false, length = 10)
    private Presenca primeiraHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "segunda_hora", nullable = false, length = 10)
    private Presenca segundaHora;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    /** Id da matrícula lido direto da coluna, sem carregar a entidade. */
    @Column(name = "matricula_id", insertable = false, updatable = false)
    private Long matriculaId;

    protected RegistroFrequenciaEntity() {
    }

    public RegistroFrequenciaEntity(AulaEntity aula, MatriculaEntity matricula) {
        this.aula = aula;
        this.matricula = matricula;
        this.matriculaId = matricula.getId();
    }

    public void marcar(Presenca primeiraHora, Presenca segundaHora, LocalDateTime atualizadoEm) {
        this.primeiraHora = primeiraHora;
        this.segundaHora = segundaHora;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public Presenca getPrimeiraHora() {
        return primeiraHora;
    }

    public Presenca getSegundaHora() {
        return segundaHora;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
