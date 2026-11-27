package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.AlunoRepository;
import br.unicatolica.ace.frequencia.application.port.CicloRepository;
import br.unicatolica.ace.frequencia.application.port.CursoRepository;
import br.unicatolica.ace.frequencia.application.port.MatriculaRepository;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.Cenario;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import br.unicatolica.ace.frequencia.domain.regra.ValidadorMatricula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static br.unicatolica.ace.frequencia.domain.Cenario.AVANCADO_I;
import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatricularAlunoTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 27);

    @Mock
    AlunoRepository alunos;
    @Mock
    TurmaRepository turmas;
    @Mock
    CursoRepository cursos;
    @Mock
    CicloRepository ciclos;
    @Mock
    MatriculaRepository matriculas;

    MatricularAluno matricularAluno;
    Aluno aluno;
    Turma turma;

    @BeforeEach
    void setUp() {
        Clock relogio = Clock.fixed(HOJE.atStartOfDay(ZoneId.of("America/Fortaleza")).toInstant(), ZoneId.of("America/Fortaleza"));
        matricularAluno = new MatricularAluno(alunos, turmas, cursos, ciclos, matriculas, ValidadorMatricula.padrao(), relogio);
        aluno = Cenario.novoAluno();
        turma = Cenario.segunda09();
    }

    private void dadosEncontrados(Long cursoId) {
        when(alunos.buscar(aluno.getNumeroMatricula())).thenReturn(Optional.of(aluno));
        when(turmas.buscar(turma.getId())).thenReturn(Optional.of(turma));
        when(cursos.buscar(cursoId)).thenReturn(Optional.of(cursoId.equals(BASICO.getId()) ? BASICO : AVANCADO_I));
        when(ciclos.buscar(1L)).thenReturn(Optional.of(Cenario.azul()));
        when(matriculas.ativasDoAluno(aluno.getNumeroMatricula())).thenReturn(List.of());
    }

    private MatricularAluno.Comando comando(Long cursoId) {
        return new MatricularAluno.Comando(aluno.getNumeroMatricula().valor(), turma.getId(), cursoId, 1L, null);
    }

    @Test
    @DisplayName("matricula o aluno com data de hoje e salva pelo repositório")
    void matriculaESalva() {
        dadosEncontrados(BASICO.getId());
        when(matriculas.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Matricula m = matricularAluno.executar(comando(BASICO.getId()));

        assertEquals(StatusMatricula.ATIVA, m.getStatus());
        assertEquals(HOJE, m.getDataInicio());
        assertEquals(HOJE.plusMonths(12), m.getDataPrevistaTermino());
        verify(matriculas).salvar(m);
    }

    @Test
    @DisplayName("6º avançado: devolve a regra violada (RN02) e não salva nada")
    void regraVioladaNaoSalva() {
        Cenario.preencher(turma, AVANCADO_I, 5);
        dadosEncontrados(AVANCADO_I.getId());

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> matricularAluno.executar(comando(AVANCADO_I.getId())));

        assertEquals("RN02", erro.getRegra());
        verify(matriculas, never()).salvar(any());
    }

    @Test
    @DisplayName("aluno inexistente: 404 antes de consultar turma, curso ou ciclo")
    void alunoInexistente() {
        when(alunos.buscar(aluno.getNumeroMatricula())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> matricularAluno.executar(comando(BASICO.getId())));
        verifyNoInteractions(turmas, cursos, ciclos, matriculas);
    }
}
