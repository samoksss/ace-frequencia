package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.application.port.AulaRepository;
import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.RegistroFrequencia;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.AulaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.RegistroFrequenciaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.AulaJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.MatriculaJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.RegistroFrequenciaJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.TurmaJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class AulaRepositoryAdapter implements AulaRepository {

    private final AulaJpa aulaJpa;
    private final RegistroFrequenciaJpa registroJpa;
    private final TurmaJpa turmaJpa;
    private final MatriculaJpa matriculaJpa;

    public AulaRepositoryAdapter(AulaJpa aulaJpa, RegistroFrequenciaJpa registroJpa, TurmaJpa turmaJpa,
                                 MatriculaJpa matriculaJpa) {
        this.aulaJpa = aulaJpa;
        this.registroJpa = registroJpa;
        this.turmaJpa = turmaJpa;
        this.matriculaJpa = matriculaJpa;
    }

    @Override
    public Optional<Aula> buscar(Turma turma, LocalDate data) {
        return aulaJpa.findByTurmaIdAndData(turma.getId(), data)
                .map(a -> Mapeador.aula(a, turma, registroJpa.findByAulaId(a.getId())));
    }

    @Override
    public List<Aula> listar(Turma turma, LocalDate de, LocalDate ate) {
        return aulaJpa.findByTurmaIdAndDataBetweenOrderByDataAsc(turma.getId(), de, ate).stream()
                .map(a -> Mapeador.aula(a, turma, registroJpa.findByAulaId(a.getId())))
                .toList();
    }

    @Override
    @Transactional
    public Aula salvar(Aula aula) {
        AulaEntity entidade = aula.getId() != null
                ? aulaJpa.findById(aula.getId()).orElseThrow()
                : aulaJpa.findByTurmaIdAndData(aula.getTurma().getId(), aula.getData())
                        .orElseGet(() -> aulaJpa.save(new AulaEntity(
                                turmaJpa.findById(aula.getTurma().getId()).orElseThrow(), aula.getData())));

        Map<Long, RegistroFrequenciaEntity> existentes = registroJpa.findByAulaId(entidade.getId()).stream()
                .collect(Collectors.toMap(RegistroFrequenciaEntity::getMatriculaId, Function.identity()));

        for (RegistroFrequencia r : aula.getRegistros()) {
            Long matriculaId = r.getMatricula().getId();
            RegistroFrequenciaEntity registro = existentes.get(matriculaId);
            if (registro == null) {
                registro = new RegistroFrequenciaEntity(entidade, matriculaJpa.findById(matriculaId).orElseThrow());
            }
            registro.marcar(r.getPrimeiraHora(), r.getSegundaHora(), r.getAtualizadoEm());
            registroJpa.save(registro);
        }
        registroJpa.flush();
        return Mapeador.aula(entidade, aula.getTurma(), registroJpa.findByAulaId(entidade.getId()));
    }
}
