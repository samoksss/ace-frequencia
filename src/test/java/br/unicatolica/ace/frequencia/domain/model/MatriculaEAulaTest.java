package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.Cenario;
import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO;
import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO_RAPIDO;
import static br.unicatolica.ace.frequencia.domain.Cenario.HOJE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatriculaEAulaTest {

    /** 23/11/2026 é uma segunda-feira. */
    private static final LocalDate SEGUNDA = LocalDate.of(2026, 11, 23);
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 26, 10, 0);

    @Nested
    @DisplayName("Matricula")
    class MatriculaTest {

        @Test
        @DisplayName("RN11: término previsto em 12 meses (regular) e 6 meses (rápido)")
        void duracaoPrevista() {
            Turma turma = Cenario.segunda09();
            assertEquals(LocalDate.of(2027, 11, 26), Cenario.matricular(turma, BASICO).getDataPrevistaTermino());
            assertEquals(LocalDate.of(2027, 5, 26), Cenario.matricular(turma, BASICO_RAPIDO).getDataPrevistaTermino());
        }

        @Test
        @DisplayName("RN10: cancelamento exige motivo")
        void cancelarExigeMotivo() {
            Matricula m = Cenario.matricular(Cenario.segunda09(), BASICO);
            RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> m.cancelar("  ", HOJE));
            assertEquals("RN10", erro.getRegra());
            assertTrue(m.isAtiva());
        }

        @Test
        @DisplayName("RN10: cancelada guarda motivo e data e não pode ser cancelada de novo")
        void cancelar() {
            Matricula m = Cenario.matricular(Cenario.segunda09(), BASICO);
            m.cancelar("Quer cancelar (contato da recepção)", HOJE);
            assertEquals(StatusMatricula.CANCELADA, m.getStatus());
            assertEquals("Quer cancelar (contato da recepção)", m.getMotivoCancelamento());
            assertEquals(HOJE, m.getDataCancelamento());
            assertThrows(RegraNegocioException.class, () -> m.cancelar("de novo", HOJE));
        }
    }

    @Nested
    @DisplayName("Aula / chamada (RN07, RN08)")
    class AulaTest {

        @Test
        @DisplayName("RN07: data em dia errado da semana é rejeitada")
        void diaErradoRejeitado() {
            Turma segunda = Cenario.segunda09();
            LocalDate terca = SEGUNDA.plusDays(1);
            RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> Aula.abrir(segunda, terca, HOJE));
            assertEquals("RN07", erro.getRegra());
            assertTrue(erro.getMessage().contains("segunda"), erro.getMessage());
        }

        @Test
        @DisplayName("RN07: data futura é rejeitada")
        void dataFuturaRejeitada() {
            Turma segunda = Cenario.segunda09();
            assertThrows(RegraNegocioException.class, () -> Aula.abrir(segunda, SEGUNDA.plusWeeks(1), HOJE));
        }

        @Test
        @DisplayName("Factory Method: abre a chamada com um registro para cada matrícula ATIVA")
        void abreComMatriculasAtivas() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, BASICO, 3);
            turma.matriculasAtivas().get(0).cancelar("Desistiu", HOJE);

            Aula aula = Aula.abrir(turma, SEGUNDA, HOJE);
            assertEquals(2, aula.getRegistros().size());
        }

        @Test
        @DisplayName("todos presentes, depois correção de um aluno guardando atualizadoEm")
        void registrarECorrigir() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, BASICO, 2);
            Aula aula = Aula.abrir(turma, SEGUNDA, HOJE);
            aula.marcarTodosPresentes(AGORA);
            assertTrue(aula.getRegistros().stream().allMatch(r -> r.resultado() == ResultadoAula.PRESENTE));

            Matricula m = turma.matriculasAtivas().get(0);
            LocalDateTime depois = AGORA.plusHours(1);
            RegistroFrequencia r = aula.registrar(m, Presenca.FALTA, Presenca.FALTA, depois);
            assertTrue(r.isFalta());
            assertEquals(depois, r.getAtualizadoEm());
            assertEquals(2, aula.getRegistros().size(), "correção não cria registro duplicado");
        }

        @Test
        @DisplayName("RN08: falta só quando ausente nas 2 horas; 1 hora = presença parcial")
        void presencaParcial() {
            assertEquals(ResultadoAula.FALTA, ResultadoAula.de(Presenca.FALTA, Presenca.FALTA));
            assertEquals(ResultadoAula.PARCIAL, ResultadoAula.de(Presenca.PRESENTE, Presenca.FALTA));
            assertEquals(ResultadoAula.PARCIAL, ResultadoAula.de(Presenca.FALTA, Presenca.PRESENTE));
            assertEquals(ResultadoAula.PRESENTE, ResultadoAula.de(Presenca.PRESENTE, Presenca.PRESENTE));
        }

        @Test
        @DisplayName("aluno matriculado depois de a aula ser lançada entra na chamada ao reabrir")
        void incluiMatriculaNova() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, BASICO, 1);
            Aula aula = Aula.abrir(turma, SEGUNDA, HOJE);
            Matricula nova = Cenario.matricular(turma, BASICO);

            aula.incluirMatriculasAtivasSemRegistro();
            assertEquals(2, aula.getRegistros().size());
            assertNotNull(aula.registroDe(nova).orElse(null));
        }

        @Test
        @DisplayName("não aceita registro de aluno de outra turma")
        void outraTurmaRejeitada() {
            Turma turma = Cenario.segunda09();
            Aula aula = Aula.abrir(turma, SEGUNDA, HOJE);
            Matricula deOutraTurma = Cenario.matricular(Cenario.turma(DayOfWeek.MONDAY, 13, 15), BASICO);
            assertThrows(DadoInvalidoException.class,
                    () -> aula.registrar(deOutraTurma, Presenca.PRESENTE, Presenca.PRESENTE, AGORA));
            assertFalse(aula.registroDe(deOutraTurma).isPresent());
        }
    }
}
