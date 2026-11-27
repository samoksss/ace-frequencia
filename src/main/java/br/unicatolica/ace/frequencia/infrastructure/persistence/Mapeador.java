package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.Ciclo;
import br.unicatolica.ace.frequencia.domain.model.Curso;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.RegistroFrequencia;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.vo.Horario;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;
import br.unicatolica.ace.frequencia.domain.vo.Telefone;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.AlunoEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.AulaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.CicloEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.CursoEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.MatriculaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.RegistroFrequenciaEntity;
import br.unicatolica.ace.frequencia.infrastructure.persistence.entity.TurmaEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converte entidades JPA em objetos do domínio (e vice-versa).
 * Mantém o domínio livre de anotações de banco (trade-off registrado no DIARIO.md).
 */
final class Mapeador {

    private Mapeador() {
    }

    static Aluno aluno(AlunoEntity e) {
        return new Aluno(NumeroMatricula.de(e.getNumeroMatricula()), e.getNome(), Telefone.de(e.getTelefone()),
                e.getObservacao());
    }

    static AlunoEntity aluno(Aluno a) {
        return new AlunoEntity(a.getNumeroMatricula().valor(), a.getNome(), a.getTelefone().digitos(), a.getObservacao());
    }

    static Curso curso(CursoEntity e) {
        return new Curso(e.getId(), e.getNome(), e.getCategoria(), e.isRapido());
    }

    static Ciclo ciclo(CicloEntity e) {
        return new Ciclo(e.getId(), e.getNome(), e.getCorHex(), e.getDataInicio(), e.getDataPrevistaTermino(),
                e.isAceitaMatriculas());
    }

    /** Reconstitui o agregado Turma com todas as suas matrículas (ativas e encerradas). */
    static Turma turma(TurmaEntity e, List<MatriculaEntity> matriculas) {
        Turma turma = new Turma(e.getId(), e.getDiaSemana(), new Horario(e.getHoraInicio(), e.getHoraFim()));
        for (MatriculaEntity m : matriculas) {
            turma.adicionarMatriculaExistente(new Matricula(m.getId(), aluno(m.getAluno()), turma, curso(m.getCurso()),
                    ciclo(m.getCiclo()), m.getDataInicio(), m.getStatus(), m.getMotivoCancelamento(),
                    m.getDataCancelamento()));
        }
        return turma;
    }

    /** Reconstitui a Aula ligando cada registro à matrícula correspondente da turma (já completa). */
    static Aula aula(AulaEntity e, Turma turma, List<RegistroFrequenciaEntity> registros) {
        Map<Long, Matricula> porId = turma.getMatriculas().stream()
                .collect(Collectors.toMap(Matricula::getId, Function.identity()));
        List<RegistroFrequencia> lista = registros.stream()
                .filter(r -> porId.containsKey(r.getMatriculaId()))
                .map(r -> new RegistroFrequencia(porId.get(r.getMatriculaId()), r.getPrimeiraHora(), r.getSegundaHora(),
                        r.getAtualizadoEm()))
                .toList();
        return Aula.existente(e.getId(), turma, e.getData(), lista);
    }
}
