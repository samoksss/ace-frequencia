package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.Cenario;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Turma;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;

import static br.unicatolica.ace.frequencia.domain.Cenario.AVANCADO_I;
import static br.unicatolica.ace.frequencia.domain.Cenario.AVANCADO_II;
import static br.unicatolica.ace.frequencia.domain.Cenario.AVANCADO_KIDS;
import static br.unicatolica.ace.frequencia.domain.Cenario.AVANCADO_RAPIDO;
import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO;
import static br.unicatolica.ace.frequencia.domain.Cenario.BASICO_RAPIDO;
import static br.unicatolica.ace.frequencia.domain.Cenario.HOJE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegrasMatriculaTest {

    private final ValidadorMatricula validador = ValidadorMatricula.padrao();

    private Matricula matricular(Turma turma, Aluno aluno, List<Matricula> ativasDoAluno,
                                 br.unicatolica.ace.frequencia.domain.model.Curso curso) {
        return turma.matricular(aluno, curso, Cenario.azul(), HOJE, ativasDoAluno, validador);
    }

    @Nested
    @DisplayName("RN01 / RN03 - capacidade de 20 alunos ativos")
    class Capacidade {

        @Test
        @DisplayName("20º aluno é aceito e o 21º é bloqueado")
        void vigesimoPrimeiroBloqueado() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, BASICO, 19);
            assertDoesNotThrow(() -> Cenario.matricular(turma, BASICO));
            assertEquals(20, turma.ocupacao());

            RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                    () -> Cenario.matricular(turma, BASICO));
            assertEquals("RN01", erro.getRegra());
            assertTrue(erro.getMessage().contains("Segunda 09–11"), erro.getMessage());
            assertEquals(20, turma.ocupacao(), "o 21º não pode entrar na turma");
        }

        @Test
        @DisplayName("RN03: aluno faltoso continua ocupando a vaga; só o cancelamento libera")
        void faltosoOcupaVaga() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, BASICO, 20);
            Matricula faltoso = turma.matriculasAtivas().get(0);

            assertThrows(RegraNegocioException.class, () -> Cenario.matricular(turma, BASICO));

            faltoso.cancelar("Não retornou após 4 faltas", HOJE);
            assertEquals(19, turma.ocupacao());
            assertDoesNotThrow(() -> Cenario.matricular(turma, BASICO));
        }
    }

    @Nested
    @DisplayName("RN02 - no máximo 5 alunos de cursos avançados")
    class CotaAvancado {

        @Test
        @DisplayName("6º avançado é bloqueado, de qualquer tipo (inclusive Kids)")
        void sextoAvancadoBloqueado() {
            Turma turma = Cenario.turma(DayOfWeek.MONDAY, 19, 21);
            Cenario.matricular(turma, AVANCADO_I);
            Cenario.matricular(turma, AVANCADO_II);
            Cenario.matricular(turma, AVANCADO_KIDS);
            Cenario.matricular(turma, AVANCADO_RAPIDO);
            Cenario.matricular(turma, AVANCADO_I);
            assertEquals(5, turma.avancadosAtivos());

            RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                    () -> Cenario.matricular(turma, AVANCADO_KIDS));
            assertEquals("RN02", erro.getRegra());
            assertEquals("Turma Segunda 19–21 já tem 5 alunos de cursos avançados (limite de computadores).",
                    erro.getMessage());
        }

        @Test
        @DisplayName("com a cota de avançados cheia, aluno do Básico ainda pode entrar")
        void basicoComCotaCheia() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, AVANCADO_I, 5);
            assertDoesNotThrow(() -> Cenario.matricular(turma, BASICO));
            assertEquals("6/20 · avançados 5/5", turma.resumoOcupacao());
        }

        @Test
        @DisplayName("avançado cancelado libera o computador")
        void cancelamentoLiberaCota() {
            Turma turma = Cenario.segunda09();
            Cenario.preencher(turma, AVANCADO_I, 5);
            turma.matriculasAtivas().get(0).cancelar("Mudou de cidade", HOJE);
            assertDoesNotThrow(() -> Cenario.matricular(turma, AVANCADO_KIDS));
        }
    }

    @Nested
    @DisplayName("RN04 - matrículas ativas por aluno")
    class LimitePorAluno {

        @Test
        @DisplayName("aluno de curso regular não pode ter 2ª matrícula ativa")
        void regularEmSegundaTurmaBloqueado() {
            Aluno aluno = Cenario.novoAluno();
            Matricula primeira = matricular(Cenario.segunda09(), aluno, List.of(), BASICO);

            RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                    () -> matricular(Cenario.turma(DayOfWeek.WEDNESDAY, 9, 11), aluno, List.of(primeira), BASICO));
            assertEquals("RN04", erro.getRegra());
        }

        @Test
        @DisplayName("aluno de curso rápido pode ter 2 matrículas em dias diferentes, e a 3ª é bloqueada")
        void rapidoEmDuasTurmas() {
            Aluno aluno = Cenario.novoAluno();
            Matricula segunda = matricular(Cenario.segunda09(), aluno, List.of(), BASICO_RAPIDO);
            Matricula quarta = assertDoesNotThrow(() ->
                    matricular(Cenario.turma(DayOfWeek.WEDNESDAY, 9, 11), aluno, List.of(segunda), BASICO_RAPIDO));

            RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                    () -> matricular(Cenario.turma(DayOfWeek.FRIDAY, 9, 11), aluno, List.of(segunda, quarta), BASICO_RAPIDO));
            assertEquals("RN04", erro.getRegra());
        }

        @Test
        @DisplayName("curso rápido: 2ª matrícula no mesmo dia da semana é bloqueada")
        void rapidoMesmoDiaBloqueado() {
            Aluno aluno = Cenario.novoAluno();
            Matricula manha = matricular(Cenario.segunda09(), aluno, List.of(), AVANCADO_RAPIDO);

            RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                    () -> matricular(Cenario.turma(DayOfWeek.MONDAY, 15, 17), aluno, List.of(manha), AVANCADO_RAPIDO));
            assertTrue(erro.getMessage().contains("outro dia"), erro.getMessage());
        }

        @Test
        @DisplayName("aluno com matrícula regular ativa não ganha 2ª matrícula só por escolher curso rápido")
        void regularMaisRapidoBloqueado() {
            Aluno aluno = Cenario.novoAluno();
            Matricula regular = matricular(Cenario.segunda09(), aluno, List.of(), BASICO);
            assertThrows(RegraNegocioException.class,
                    () -> matricular(Cenario.turma(DayOfWeek.FRIDAY, 9, 11), aluno, List.of(regular), BASICO_RAPIDO));
        }

        @Test
        @DisplayName("matrícula cancelada não conta para o limite")
        void canceladaNaoConta() {
            Aluno aluno = Cenario.novoAluno();
            Matricula antiga = matricular(Cenario.segunda09(), aluno, List.of(), BASICO);
            antiga.cancelar("Trocou de horário", HOJE);
            assertDoesNotThrow(() ->
                    matricular(Cenario.turma(DayOfWeek.TUESDAY, 13, 15), aluno, List.of(antiga), BASICO));
        }
    }

    @Nested
    @DisplayName("RN06 - ciclo que não aceita matrículas")
    class CicloEncerrando {

        @Test
        @DisplayName("ciclo Laranja (encerrando) bloqueia nova matrícula")
        void laranjaBloqueado() {
            Turma turma = Cenario.segunda09();
            RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                    () -> turma.matricular(Cenario.novoAluno(), BASICO, Cenario.laranja(), HOJE, List.of(), validador));
            assertEquals("RN06", erro.getRegra());
            assertTrue(erro.getMessage().contains("Laranja"));
            assertEquals(0, turma.ocupacao());
        }
    }

    @Nested
    @DisplayName("ValidadorMatricula (Composite)")
    class Composite {

        @Test
        @DisplayName("agrega as 4 regras e permite plugar uma regra nova sem alterar as existentes")
        void regraPlugavel() {
            assertEquals(4, ValidadorMatricula.padrao().getRegras().size());

            RegraMatricula semSabado = s -> {
                if (s.turma().getDiaSemana() == DayOfWeek.SATURDAY) {
                    throw new RegraNegocioException("TESTE", "Sem matrículas no sábado");
                }
            };
            ValidadorMatricula estendido = new ValidadorMatricula(List.of(ValidadorMatricula.padrao(), semSabado));
            Turma sabado = Cenario.turma(DayOfWeek.SATURDAY, 9, 11);
            assertThrows(RegraNegocioException.class,
                    () -> sabado.matricular(Cenario.novoAluno(), BASICO, Cenario.azul(), HOJE, List.of(), estendido));
        }
    }
}
