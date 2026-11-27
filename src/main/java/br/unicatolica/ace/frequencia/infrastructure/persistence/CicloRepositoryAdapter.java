package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.application.port.CicloRepository;
import br.unicatolica.ace.frequencia.domain.model.Ciclo;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.CicloEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.CicloJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CicloRepositoryAdapter implements CicloRepository {

    private final CicloJpa jpa;

    public CicloRepositoryAdapter(CicloJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Ciclo> buscar(Long id) {
        return id == null ? Optional.empty() : jpa.findById(id).map(Mapeador::ciclo);
    }

    @Override
    public List<Ciclo> listar() {
        return jpa.findAllByOrderByDataInicioAsc().stream().map(Mapeador::ciclo).toList();
    }

    @Override
    @Transactional
    public Ciclo salvar(Ciclo c) {
        CicloEntity salvo = jpa.save(new CicloEntity(c.getId(), c.getNome(), c.getCorHex(), c.getDataInicio(),
                c.getDataPrevistaTermino(), c.isAceitaMatriculas()));
        return Mapeador.ciclo(salvo);
    }
}
