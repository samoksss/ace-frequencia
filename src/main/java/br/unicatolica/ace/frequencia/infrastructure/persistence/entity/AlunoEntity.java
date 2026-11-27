package br.unicatolica.ace.frequencia.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluno")
public class AlunoEntity {

    @Id
    @Column(name = "numero_matricula", length = 12)
    private String numeroMatricula;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 11)
    private String telefone;

    @Column(length = 500)
    private String observacao;

    protected AlunoEntity() {
    }

    public AlunoEntity(String numeroMatricula, String nome, String telefone, String observacao) {
        this.numeroMatricula = numeroMatricula;
        this.nome = nome;
        this.telefone = telefone;
        this.observacao = observacao;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getObservacao() {
        return observacao;
    }
}
