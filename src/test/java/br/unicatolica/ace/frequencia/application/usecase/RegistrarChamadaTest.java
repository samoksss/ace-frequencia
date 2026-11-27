package br.unicatolica.ace.frequencia.application.usecase;

import br.unicatolica.ace.frequencia.application.port.AulaRepository;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.domain.Cenario;
import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Presenca;
import br.unicatolica.ace.frequencia.domain.model.RegistroFrequencia;
import br.unicatolica.ace.frequencia.domain.model.ResultadoAula;
import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarChamadaTest {

    /** Sexta, 27/11/2026, 10h. A turma é de segunda: a aula de 23/11 já passou. */
    private static final ZoneId ZONA = ZoneId.of("America/Fortaleza");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 27, 10, 0);
    private static final LocalDate SEGUNDA = LocalDate.of(2026, 11, 23);

    @Mock
    TurmaRepository turmas;
    @Mock
    AulaRepository aulas;

    RegistrarChamada registrarChamada;
    Turma turma;
    Matricula ana;
    Matricula bruno;

    @BeforeEach
    void setUp() {
        registrarChamada = new RegistrarChamada(turmas, aulas, Clock.fixed(AGORA.atZone(ZONA).toInstant(), ZONA));
        turma = Cenario.segunda09();
        ana = matriculaSalva(1L);
        bruno = matriculaSalva(2L);
        when(turmas.buscar(turma.getId())).thenReturn(Optional.of(turma));
    }

    /** Matrícula como viria do banco (com id), para a chamada poder localizar pelo id. */
    private Matricula matriculaSalva(Long id) {
        Matricula m = new Matricula(id, Cenario.novoAluno(), turma, BASICO, Cenario.azul(), SEGUNDA.minusMonths(1),
                StatusMatricula.ATIVA, null, null);
        turma.adicionarMatriculaExistente(m);
        return m;
    }

    @Test
    @DisplayName("chamada nova: marca a falta enviada, os demais ficam PRESENTE e todos recebem atualizadoEm")
    void salvaChamadaNova() {
        when(aulas.buscar(turma, SEGUNDA)).thenReturn(Optional.empty());
        when(aulas.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        registrarChamada.salvar(turma.getId(), SEGUNDA,
                List.of(new RegistrarChamada.Marcacao(2L, Presenca.FALTA, Presenca.FALTA)));

        ArgumentCaptor<Aula> salva = ArgumentCaptor.forClass(Aula.class);
        verify(aulas).salvar(salva.capture());
        Aula aula = salva.getValue();
        assertEquals(ResultadoAula.PRESENTE, aula.registroDe(ana).orElseThrow().resultado());
        assertEquals(ResultadoAula.FALTA, aula.registroDe(bruno).orElseThrow().resultado());
        assertTrue(aula.getRegistros().stream().map(RegistroFrequencia::getAtualizadoEm).allMatch(AGORA::equals));
    }

    @Test
    @DisplayName("abrir data sem chamada lançada devolve chamada nova (não salva) com todos PRESENTE")
    void abreSemSalvar() {
        when(aulas.buscar(turma, SEGUNDA)).thenReturn(Optional.empty());

        Aula aula = registrarChamada.abrir(turma.getId(), SEGUNDA);

        assertEquals(2, aula.getRegistros().size());
        assertTrue(aula.getRegistros().stream().noneMatch(RegistroFrequencia::isFalta));
        verify(aulas, never()).salvar(any());
    }

    @Test
    @DisplayName("data em outro dia da semana: RN07 e nada é salvo")
    void diaErrado() {
        LocalDate terca = SEGUNDA.plusDays(1);
        when(aulas.buscar(turma, terca)).thenReturn(Optional.empty());

        assertThrows(RegraNegocioException.class,
                () -> registrarChamada.salvar(turma.getId(), terca, List.of()));
        verify(aulas, never()).salvar(any());
    }

    @Test
    @DisplayName("matrícula que não é da turma: 400")
    void matriculaDeOutraTurma() {
        when(aulas.buscar(turma, SEGUNDA)).thenReturn(Optional.empty());

        assertThrows(DadoInvalidoException.class, () -> registrarChamada.salvar(turma.getId(), SEGUNDA,
                List.of(new RegistrarChamada.Marcacao(999L, Presenca.FALTA, Presenca.FALTA))));
        verify(aulas, never()).salvar(any());
        assertFalse(turma.getMatriculas().isEmpty());
    }
}
