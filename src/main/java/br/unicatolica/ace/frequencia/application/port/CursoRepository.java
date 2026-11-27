package br.unicatolica.ace.frequencia.application.port;

import br.unicatolica.ace.frequencia.domain.model.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository {

    Optional<Curso> buscar(Long id);

    List<Curso> listar();

    Curso salvar(Curso curso);
}
