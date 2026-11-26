package br.unicatolica.ace.frequencia.domain.vo;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.model.Turno;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueObjectsTest {

    @Nested
    @DisplayName("NumeroMatricula")
    class NumeroMatriculaTest {

        @Test
        @DisplayName("aceita código com letra e normaliza para maiúscula (ex.: 7102j → 7102J)")
        void normaliza() {
            assertEquals("7102J", NumeroMatricula.de(" 7102j ").valor());
            assertEquals(NumeroMatricula.de("9305"), NumeroMatricula.de("9305"));
        }

        @Test
        @DisplayName("rejeita vazio e caracteres especiais")
        void rejeitaInvalido() {
            assertThrows(DadoInvalidoException.class, () -> NumeroMatricula.de(" "));
            assertThrows(DadoInvalidoException.class, () -> NumeroMatricula.de("93-05"));
        }
    }

    @Nested
    @DisplayName("Telefone")
    class TelefoneTest {

        @Test
        @DisplayName("guarda só dígitos, formata para a tela e gera o número do WhatsApp")
        void formata() {
            Telefone tel = Telefone.de("(88) 9 0000-0123");
            assertEquals("88900000123", tel.digitos());
            assertEquals("(88) 9 0000-0123", tel.formatado());
            assertEquals("5588900000123", tel.paraWhatsApp());
        }

        @Test
        @DisplayName("aceita número com +55 na frente")
        void aceitaDdi() {
            assertEquals("88900000123", Telefone.de("+55 88 90000-0123").digitos());
        }

        @Test
        @DisplayName("rejeita telefone sem DDD")
        void rejeitaSemDdd() {
            assertThrows(DadoInvalidoException.class, () -> Telefone.de("90000-0123"));
        }
    }

    @Nested
    @DisplayName("Horario e Turno")
    class HorarioTest {

        @Test
        @DisplayName("turno derivado da hora de início: manhã < 12h, tarde 12h–17h59, noite >= 18h")
        void turno() {
            assertEquals(Turno.MANHA, Horario.de(9, 11).turno());
            assertEquals(Turno.MANHA, new Horario(LocalTime.of(11, 59), LocalTime.of(13, 0)).turno());
            assertEquals(Turno.TARDE, Horario.de(13, 15).turno());
            assertEquals(Turno.TARDE, Horario.de(17, 19).turno());
            assertEquals(Turno.NOITE, Horario.de(19, 21).turno());
        }

        @Test
        @DisplayName("descrição igual à planilha (09–11)")
        void descricao() {
            assertEquals("09–11", Horario.de(9, 11).descricao());
        }

        @Test
        @DisplayName("rejeita fim antes do início")
        void rejeitaFimAntesDoInicio() {
            assertThrows(DadoInvalidoException.class, () -> Horario.de(11, 9));
        }
    }
}
