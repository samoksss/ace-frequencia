package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.application.port.MatriculaRepository;
import br.unicatolica.ace.frequencia.domain.Cenario;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarMatriculaTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 27);

    @Mock
    MatriculaRepository matriculas;

    CancelarMatricula cancelarMatricula;

    @BeforeEach
    void setUp() {
        ZoneId zona = ZoneId.of("America/Fortaleza");
        cancelarMatricula = new CancelarMatricula(matriculas, Clock.fixed(HOJE.atStartOfDay(zona).toInstant(), zona));
    }

    @Test
    @DisplayName("cancela com motivo e data de hoje, liberando a vaga (RN03/RN10)")
    void cancela() {
        Turma turma = Cenario.segunda09();
        Matricula m = Cenario.matricular(turma, BASICO);
        when(matriculas.buscar(10L)).thenReturn(Optional.of(m));
        when(matriculas.salvar(m)).thenReturn(m);

        Matricula cancelada = cancelarMatricula.executar(10L, "Mudou de cidade");

        assertEquals(StatusMatricula.CANCELADA, cancelada.getStatus());
        assertEquals("Mudou de cidade", cancelada.getMotivoCancelamento());
        assertEquals(HOJE, cancelada.getDataCancelamento());
        assertEquals(0, turma.ocupacao());
        verify(matriculas).salvar(m);
    }

    @Test
    @DisplayName("sem motivo: RN10 e nada é salvo")
    void semMotivo() {
        Matricula m = Cenario.matricular(Cenario.segunda09(), BASICO);
        when(matriculas.buscar(10L)).thenReturn(Optional.of(m));

        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> cancelarMatricula.executar(10L, " "));
        assertEquals("RN10", erro.getRegra());
        verify(matriculas, never()).salvar(any());
    }

    @Test
    @DisplayName("matrícula inexistente: 404")
    void inexistente() {
        when(matriculas.buscar(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> cancelarMatricula.executar(99L, "x"));
    }
}
