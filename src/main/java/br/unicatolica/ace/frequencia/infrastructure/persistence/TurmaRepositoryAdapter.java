package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.MatriculaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.TurmaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.MatriculaJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.TurmaJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class TurmaRepositoryAdapter implements TurmaRepository {

    private final TurmaJpa turmaJpa;
    private final MatriculaJpa matriculaJpa;

    public TurmaRepositoryAdapter(TurmaJpa turmaJpa, MatriculaJpa matriculaJpa) {
        this.turmaJpa = turmaJpa;
        this.matriculaJpa = matriculaJpa;
    }

    @Override
    public Optional<Turma> buscar(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return turmaJpa.findById(id).map(t -> Mapeador.turma(t, matriculaJpa.findByTurmaId(id)));
    }

    @Override
    public List<Turma> listar() {
        Map<Long, List<MatriculaEntity>> porTurma = matriculaJpa.findAll().stream()
                .collect(Collectors.groupingBy(m -> m.getTurma().getId()));
        return turmaJpa.findAll().stream()
                .map(t -> Mapeador.turma(t, porTurma.getOrDefault(t.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public Turma salvar(Turma turma) {
        TurmaEntity salva = turmaJpa.save(new TurmaEntity(turma.getId(), turma.getDiaSemana(),
                turma.getHorario().inicio(), turma.getHorario().fim()));
        return Mapeador.turma(salva, salva.getId() == null ? List.of() : matriculaJpa.findByTurmaId(salva.getId()));
    }
}
