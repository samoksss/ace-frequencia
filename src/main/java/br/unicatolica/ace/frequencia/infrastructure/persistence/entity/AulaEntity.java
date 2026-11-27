package br.unicatolica.ace.frequencia.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/** Uma aula por turma e data (RN07). */
@Entity
@Table(name = "aula", uniqueConstraints = @UniqueConstraint(name = "uk_aula_turma_data", columnNames = {"turma_id", "data"}))
public class AulaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private TurmaEntity turma;

    @Column(nullable = false)
    private LocalDate data;

    protected AulaEntity() {
    }

    public AulaEntity(TurmaEntity turma, LocalDate data) {
        this.turma = turma;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public TurmaEntity getTurma() {
        return turma;
    }

    public LocalDate getData() {
        return data;
    }
}
