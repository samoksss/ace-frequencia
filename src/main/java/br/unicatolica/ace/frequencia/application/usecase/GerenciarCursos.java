package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.CursoRepository;
import br.unicatolica.ace.frequencia.domain.model.CategoriaCurso;
import br.unicatolica.ace.frequencia.domain.model.Curso;

import java.util.List;

/** Catálogo de cursos (cadastro simples; no MVP os cursos vêm da carga inicial). */
public class GerenciarCursos {

    public record Comando(String nome, CategoriaCurso categoria, boolean rapido) {
    }

    private final CursoRepository cursos;

    public GerenciarCursos(CursoRepository cursos) {
        this.cursos = cursos;
    }

    public List<Curso> listar() {
        return cursos.listar();
    }

    public Curso buscar(Long id) {
        return cursos.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Curso", id));
    }

    public Curso cadastrar(Comando c) {
        return cursos.salvar(new Curso(null, c.nome(), c.categoria(), c.rapido()));
    }
}
