package br.unicatolica.ace.frequencia.application.port;

import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;

import java.util.List;
import java.util.Optional;

/** Porta de saída do agregado Aluno (implementada na infrastructure: GoF Adapter). */
public interface AlunoRepository {

    Optional<Aluno> buscar(NumeroMatricula numero);

    boolean existe(NumeroMatricula numero);

    /** Busca por parte do nome ou do número de matrícula; texto vazio lista todos. */
    List<Aluno> pesquisar(String texto);

    Aluno salvar(Aluno aluno);
}
