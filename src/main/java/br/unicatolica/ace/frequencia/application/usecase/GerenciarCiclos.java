package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.CicloRepository;
import br.unicatolica.ace.frequencia.domain.model.Ciclo;

import java.time.LocalDate;
import java.util.List;

/** Ciclos de início identificados por cor (Amarela, Azul, Laranja...). */
public class GerenciarCiclos {

    public record Comando(String nome, String corHex, LocalDate dataInicio, LocalDate dataPrevistaTermino,
                          boolean aceitaMatriculas) {
    }

    private final CicloRepository ciclos;

    public GerenciarCiclos(CicloRepository ciclos) {
        this.ciclos = ciclos;
    }

    public List<Ciclo> listar() {
        return ciclos.listar();
    }

    public Ciclo buscar(Long id) {
        return ciclos.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ciclo", id));
    }

    public Ciclo cadastrar(Comando c) {
        return ciclos.salvar(new Ciclo(null, c.nome(), c.corHex(), c.dataInicio(), c.dataPrevistaTermino(),
                c.aceitaMatriculas()));
    }

    public Ciclo atualizar(Long id, Comando c) {
        Ciclo ciclo = buscar(id);
        ciclo.atualizar(c.nome(), c.corHex(), c.dataInicio(), c.dataPrevistaTermino(), c.aceitaMatriculas());
        return ciclos.salvar(ciclo);
    }
}
