package br.unicatolica.ace.frequencia.infrastructure.persistence.entity;

import br.unicatolica.ace.frequencia.domain.model.CategoriaCurso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "curso")
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaCurso categoria;

    @Column(nullable = false)
    private boolean rapido;

    protected CursoEntity() {
    }

    public CursoEntity(Long id, String nome, CategoriaCurso categoria, boolean rapido) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.rapido = rapido;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CategoriaCurso getCategoria() {
        return categoria;
    }

    public boolean isRapido() {
        return rapido;
    }
}
