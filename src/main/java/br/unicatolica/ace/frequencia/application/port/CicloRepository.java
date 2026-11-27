package br.unicatolica.ace.frequencia.application.port;

import br.unicatolica.ace.frequencia.domain.model.Ciclo;

import java.util.List;
import java.util.Optional;

public interface CicloRepository {

    Optional<Ciclo> buscar(Long id);

    List<Ciclo> listar();

    Ciclo salvar(Ciclo ciclo);
}
