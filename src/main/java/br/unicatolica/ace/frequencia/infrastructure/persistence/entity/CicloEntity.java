package br.unicatolica.ace.frequencia.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "ciclo")
public class CicloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(name = "cor_hex", nullable = false, length = 7)
    private String corHex;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_prevista_termino", nullable = false)
    private LocalDate dataPrevistaTermino;

    @Column(name = "aceita_matriculas", nullable = false)
    private boolean aceitaMatriculas;

    protected CicloEntity() {
    }

    public CicloEntity(Long id, String nome, String corHex, LocalDate dataInicio, LocalDate dataPrevistaTermino,
                       boolean aceitaMatriculas) {
        this.id = id;
        this.nome = nome;
        this.corHex = corHex;
        this.dataInicio = dataInicio;
        this.dataPrevistaTermino = dataPrevistaTermino;
        this.aceitaMatriculas = aceitaMatriculas;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCorHex() {
        return corHex;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public boolean isAceitaMatriculas() {
        return aceitaMatriculas;
    }
}
