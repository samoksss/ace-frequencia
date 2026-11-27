package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.application.port.CursoRepository;
import br.unicatolica.ace.frequencia.domain.model.Curso;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.CursoEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.CursoJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CursoRepositoryAdapter implements CursoRepository {

    private final CursoJpa jpa;

    public CursoRepositoryAdapter(CursoJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Curso> buscar(Long id) {
        return id == null ? Optional.empty() : jpa.findById(id).map(Mapeador::curso);
    }

    @Override
    public List<Curso> listar() {
        return jpa.findAllByOrderByNomeAsc().stream().map(Mapeador::curso).toList();
    }

    @Override
    @Transactional
    public Curso salvar(Curso curso) {
        CursoEntity salvo = jpa.save(new CursoEntity(curso.getId(), curso.getNome(), curso.getCategoria(), curso.isRapido()));
        return Mapeador.curso(salvo);
    }
}
