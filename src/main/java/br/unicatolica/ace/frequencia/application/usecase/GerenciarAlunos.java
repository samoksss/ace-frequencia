package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.ConflitoException;
import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.AlunoRepository;
import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;
import br.unicatolica.ace.frequencia.domain.vo.Telefone;

import java.util.List;

/** Casos de uso: cadastrar, alterar e pesquisar alunos. */
public class GerenciarAlunos {

    public record Comando(String numeroMatricula, String nome, String telefone, String observacao) {
    }

    private final AlunoRepository alunos;

    public GerenciarAlunos(AlunoRepository alunos) {
        this.alunos = alunos;
    }

    /** RN05: o número de matrícula é único. */
    public Aluno cadastrar(Comando c) {
        NumeroMatricula numero = NumeroMatricula.de(c.numeroMatricula());
        if (alunos.existe(numero)) {
            throw new ConflitoException("numeroMatricula",
                    "Já existe um aluno com o número de matrícula " + numero + ".");
        }
        return alunos.salvar(new Aluno(numero, c.nome(), Telefone.de(c.telefone()), c.observacao()));
    }

    /** O número de matrícula não muda; só nome, telefone e observação. */
    public Aluno atualizar(String numeroMatricula, Comando c) {
        Aluno aluno = buscar(numeroMatricula);
        aluno.atualizar(c.nome(), Telefone.de(c.telefone()), c.observacao());
        return alunos.salvar(aluno);
    }

    public Aluno buscar(String numeroMatricula) {
        NumeroMatricula numero = NumeroMatricula.de(numeroMatricula);
        return alunos.buscar(numero).orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", numero));
    }

    public List<Aluno> pesquisar(String texto) {
        return alunos.pesquisar(texto == null ? "" : texto.trim());
    }
}
