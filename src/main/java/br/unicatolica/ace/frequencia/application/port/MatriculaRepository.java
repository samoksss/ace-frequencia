package br.unicatolica.ace.frequencia.application.port;

import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository {

    /** A matrícula vem ligada à sua turma completa (com as demais matrículas). */
    Optional<Matricula> buscar(Long id);

    /** Matrículas ATIVAS do aluno, em qualquer turma (para a RN04). */
    List<Matricula> ativasDoAluno(NumeroMatricula numero);

    /** Cria ou atualiza (status/cancelamento) e devolve a matrícula com id. */
    Matricula salvar(Matricula matricula);
}
