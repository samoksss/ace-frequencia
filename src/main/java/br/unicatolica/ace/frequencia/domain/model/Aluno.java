package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;
import br.unicatolica.ace.frequencia.domain.vo.Telefone;

/**
 * Aluno da New Center, identificado pelo número de matrícula da escola (RN05: único).
 * Agregado próprio: as matrículas em turmas ficam no agregado Turma.
 */
public class Aluno {

    private final NumeroMatricula numeroMatricula;
    private String nome;
    private Telefone telefone;
    private String observacao;

    public Aluno(NumeroMatricula numeroMatricula, String nome, Telefone telefone, String observacao) {
        this.numeroMatricula = Textos.obrigatorio(numeroMatricula, "numeroMatricula", "Informe o número de matrícula do aluno.");
        atualizar(nome, telefone, observacao);
    }

    public static Aluno novo(String numeroMatricula, String nome, String telefone) {
        return new Aluno(NumeroMatricula.de(numeroMatricula), nome, Telefone.de(telefone), null);
    }

    public void atualizar(String nome, Telefone telefone, String observacao) {
        this.nome = Textos.obrigatorio(nome, "nome", "Informe o nome completo do aluno.");
        this.telefone = Textos.obrigatorio(telefone, "telefone", "Informe o telefone de contato do aluno.");
        this.observacao = Textos.opcional(observacao);
    }

    public NumeroMatricula getNumeroMatricula() {
        return numeroMatricula;
    }

    public String getNome() {
        return nome;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public String getObservacao() {
        return observacao;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Aluno outro && numeroMatricula.equals(outro.numeroMatricula);
    }

    @Override
    public int hashCode() {
        return numeroMatricula.hashCode();
    }

    @Override
    public String toString() {
        return nome + " (" + numeroMatricula + ")";
    }
}
