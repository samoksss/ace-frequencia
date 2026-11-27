package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.application.port.MatriculaRepository;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.MatriculaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.AlunoJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.CicloJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.CursoJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.MatriculaJpa;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.TurmaJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class MatriculaRepositoryAdapter implements MatriculaRepository {

    private final MatriculaJpa matriculaJpa;
    private final AlunoJpa alunoJpa;
    private final TurmaJpa turmaJpa;
    private final CursoJpa cursoJpa;
    private final CicloJpa cicloJpa;

    public MatriculaRepositoryAdapter(MatriculaJpa matriculaJpa, AlunoJpa alunoJpa, TurmaJpa turmaJpa,
                                      CursoJpa cursoJpa, CicloJpa cicloJpa) {
        this.matriculaJpa = matriculaJpa;
        this.alunoJpa = alunoJpa;
        this.turmaJpa = turmaJpa;
        this.cursoJpa = cursoJpa;
        this.cicloJpa = cicloJpa;
    }

    @Override
    public Optional<Matricula> buscar(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return matriculaJpa.findById(id).map(this::comTurmaCompleta);
    }

    @Override
    public List<Matricula> ativasDoAluno(NumeroMatricula numero) {
        return matriculaJpa.findByAlunoNumeroMatriculaAndStatus(numero.valor(), StatusMatricula.ATIVA).stream()
                .map(this::comTurmaCompleta)
                .toList();
    }

    @Override
    @Transactional
    public Matricula salvar(Matricula m) {
        MatriculaEntity e = m.getId() == null
                ? new MatriculaEntity()
                : matriculaJpa.findById(m.getId()).orElseThrow();
        e.preencher(
                alunoJpa.findById(m.getAluno().getNumeroMatricula().valor()).orElseThrow(),
                turmaJpa.findById(m.getTurma().getId()).orElseThrow(),
                cursoJpa.findById(m.getCurso().getId()).orElseThrow(),
                cicloJpa.findById(m.getCiclo().getId()).orElseThrow(),
                m.getDataInicio(), m.getStatus(), m.getMotivoCancelamento(), m.getDataCancelamento());
        return comTurmaCompleta(matriculaJpa.save(e));
    }

    /** Devolve a matrícula como parte do agregado Turma completo (para ocupação, nome da turma etc.). */
    private Matricula comTurmaCompleta(MatriculaEntity e) {
        Long turmaId = e.getTurma().getId();
        Turma turma = Mapeador.turma(e.getTurma(), matriculaJpa.findByTurmaId(turmaId));
        return turma.getMatriculas().stream()
                .filter(m -> e.getId().equals(m.getId()))
                .findFirst()
                .orElseThrow();
    }
}
