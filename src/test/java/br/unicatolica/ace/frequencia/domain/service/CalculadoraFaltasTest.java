package br.unicatolica.ace.frequencia.domain.service;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.model.NivelAlerta;
import br.unicatolica.ace.frequencia.domain.model.Presenca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.unicatolica.ace.frequencia.domain.model.Presenca.FALTA;
import static br.unicatolica.ace.frequencia.domain.model.Presenca.PRESENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** RN08 e RN09: faltas consecutivas e nível de alerta. */
class CalculadoraFaltasTest {

    private static final LocalDate PRIMEIRA_AULA = LocalDate.of(2026, 10, 12); // segunda-feira
    private final CalculadoraFaltas calculadora = CalculadoraFaltas.padrao();

    /** Monta o histórico semanal; cada item é {1ª hora, 2ª hora}, da aula mais antiga para a mais recente. */
    private static List<FrequenciaNaAula> semanas(Presenca[]... aulas) {
        List<FrequenciaNaAula> historico = new ArrayList<>();
        for (int i = 0; i < aulas.length; i++) {
            historico.add(FrequenciaNaAula.de(PRIMEIRA_AULA.plusWeeks(i), aulas[i][0], aulas[i][1]));
        }
        return historico;
    }

    private static Presenca[] veio() {
        return new Presenca[]{PRESENTE, PRESENTE};
    }

    private static Presenca[] faltou() {
        return new Presenca[]{FALTA, FALTA};
    }

    private static Presenca[] parcial() {
        return new Presenca[]{PRESENTE, FALTA};
    }

    @Test
    @DisplayName("1 falta recente: sem alerta")
    void umaFalta() {
        SituacaoFrequencia s = calculadora.calcular(semanas(veio(), veio(), faltou()));
        assertEquals(1, s.faltasConsecutivas());
        assertEquals(NivelAlerta.NENHUM, s.nivel());
        assertFalse(s.precisaContato());
    }

    @Test
    @DisplayName("2 faltas seguidas = ATENÇÃO")
    void duasFaltasAtencao() {
        SituacaoFrequencia s = calculadora.calcular(semanas(veio(), faltou(), faltou()));
        assertEquals(2, s.faltasConsecutivas());
        assertEquals(NivelAlerta.ATENCAO, s.nivel());
    }

    @Test
    @DisplayName("3 ou mais faltas seguidas = CRÍTICO")
    void tresFaltasCritico() {
        assertEquals(NivelAlerta.CRITICO, calculadora.calcular(semanas(veio(), faltou(), faltou(), faltou())).nivel());
        SituacaoFrequencia quatro = calculadora.calcular(semanas(faltou(), faltou(), faltou(), faltou()));
        assertEquals(4, quatro.faltasConsecutivas());
        assertEquals(NivelAlerta.CRITICO, quatro.nivel());
        assertNull(quatro.ultimaPresenca(), "nunca veio");
    }

    @Test
    @DisplayName("presença parcial (só 1 hora) zera a sequência de faltas")
    void parcialZeraSequencia() {
        SituacaoFrequencia s = calculadora.calcular(semanas(faltou(), faltou(), faltou(), parcial()));
        assertEquals(0, s.faltasConsecutivas());
        assertEquals(3, s.totalFaltas());
        assertEquals(NivelAlerta.NENHUM, s.nivel());
        assertEquals(PRIMEIRA_AULA.plusWeeks(3), s.ultimaPresenca());
    }

    @Test
    @DisplayName("faltas antigas não contam como consecutivas se o aluno voltou depois")
    void faltasAntigasNaoContam() {
        SituacaoFrequencia s = calculadora.calcular(semanas(faltou(), faltou(), faltou(), veio(), faltou()));
        assertEquals(1, s.faltasConsecutivas());
        assertEquals(4, s.totalFaltas());
        assertEquals(PRIMEIRA_AULA.plusWeeks(3), s.ultimaPresenca());
    }

    @Test
    @DisplayName("a ordem de entrada do histórico não importa (ordena pela data)")
    void ordemNaoImporta() {
        List<FrequenciaNaAula> historico = new ArrayList<>(semanas(veio(), faltou(), faltou()));
        java.util.Collections.reverse(historico);
        assertEquals(NivelAlerta.ATENCAO, calculadora.calcular(historico).nivel());
    }

    @Test
    @DisplayName("limites configuráveis (ex.: 3 = atenção, 5 = crítico)")
    void limitesConfiguraveis() {
        CalculadoraFaltas outra = new CalculadoraFaltas(new LimitesAlerta(3, 5), new FaltasConsecutivasCriterio());
        assertEquals(NivelAlerta.NENHUM, outra.calcular(semanas(faltou(), faltou())).nivel());
        assertEquals(NivelAlerta.ATENCAO, outra.calcular(semanas(faltou(), faltou(), faltou())).nivel());
        assertThrows(DadoInvalidoException.class, () -> new LimitesAlerta(3, 3));
    }

    @Test
    @DisplayName("Strategy: trocar o critério para 'total de faltas no período' sem mudar a calculadora")
    void trocarCriterio() {
        CalculadoraFaltas porTotal = new CalculadoraFaltas(LimitesAlerta.PADRAO, new TotalFaltasCriterio());
        List<FrequenciaNaAula> alternado = semanas(faltou(), veio(), faltou(), veio(), faltou(), veio());
        assertEquals(NivelAlerta.NENHUM, calculadora.calcular(alternado).nivel());
        assertEquals(NivelAlerta.CRITICO, porTotal.calcular(alternado).nivel());
    }
}
