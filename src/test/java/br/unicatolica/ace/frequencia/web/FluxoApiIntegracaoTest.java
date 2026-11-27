package br.unicatolica.ace.frequencia.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração: sobe a aplicação inteira (API + casos de uso + JPA + H2 em memória)
 * e percorre o fluxo da demonstração. Dados fictícios.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FluxoApiIntegracaoTest {

    @Autowired
    MockMvc mvc;

    private ResultActions enviar(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder req,
                                 String json) throws Exception {
        return mvc.perform(req.contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static long id(ResultActions r, String caminho) throws Exception {
        Number n = JsonPath.read(r.andReturn().getResponse().getContentAsString(), caminho);
        return n.longValue();
    }

    @Test
    @DisplayName("fluxo completo: cadastros, matrícula, regras 409/422, chamada, histórico e cancelamento")
    void fluxoCompleto() throws Exception {
        // cadastros básicos
        long basico = id(enviar(post("/api/cursos"), """
                {"nome":"Basico Fluxo","categoria":"BASICO","rapido":false}""")
                .andExpect(status().isCreated()), "$.id");
        long azul = id(enviar(post("/api/ciclos"), """
                {"nome":"Azul Fluxo","corHex":"#1e88e5","dataInicio":"2026-08-03","dataPrevistaTermino":"2027-07-31","aceitaMatriculas":true}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.corHex").value("#1E88E5")), "$.id");
        long laranja = id(enviar(post("/api/ciclos"), """
                {"nome":"Laranja Fluxo","corHex":"#FB8C00","dataInicio":"2025-12-01","dataPrevistaTermino":"2026-12-20","aceitaMatriculas":false}""")
                .andExpect(status().isCreated()), "$.id");
        long turma = id(enviar(post("/api/turmas"), """
                {"diaSemana":"MONDAY","horaInicio":"09:00","horaFim":"11:00"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", startsWith("Segunda 09")))
                .andExpect(jsonPath("$.turno").value("MANHA"))
                .andExpect(jsonPath("$.ocupacao").value(0)), "$.id");

        // alunos (RN05: número único -> 409)
        enviar(post("/api/alunos"), """
                {"numeroMatricula":"F001","nome":"Aluna Ficticia Um","telefone":"(88) 9 0000-0001"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.whatsapp").value("5588900000001"));
        enviar(post("/api/alunos"), """
                {"numeroMatricula":"f001","nome":"Outro Nome","telefone":"(88) 9 0000-0009"}""")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        enviar(post("/api/alunos"), """
                {"numeroMatricula":"F002","nome":"Aluno Ficticio Dois","telefone":"88900000002"}""")
                .andExpect(status().isCreated());

        // matrícula OK
        long matricula = id(enviar(post("/api/matriculas"), """
                {"alunoNumero":"F001","turmaId":%d,"cursoId":%d,"cicloId":%d}""".formatted(turma, basico, azul))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVA"))
                .andExpect(jsonPath("$.cicloCor").value("#1E88E5")), "$.id");

        // RN06: ciclo Laranja não aceita matrícula -> 422
        enviar(post("/api/matriculas"), """
                {"alunoNumero":"F002","turmaId":%d,"cursoId":%d,"cicloId":%d}""".formatted(turma, basico, laranja))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.campos.regra").value("RN06"))
                .andExpect(jsonPath("$.mensagem", containsString("Laranja")));

        // RN04: aluno de curso regular não pode ter 2ª matrícula -> 422
        enviar(post("/api/matriculas"), """
                {"alunoNumero":"F001","turmaId":%d,"cursoId":%d,"cicloId":%d}""".formatted(turma, basico, azul))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.campos.regra").value("RN04"));

        mvc.perform(get("/api/turmas/{id}", turma))
                .andExpect(jsonPath("$.ocupacao").value(1))
                .andExpect(jsonPath("$.vagas").value(19));
        mvc.perform(get("/api/turmas/{id}/matriculas", turma))
                .andExpect(jsonPath("$", hasSize(1)));

        // chamada: última segunda-feira (hoje, se for segunda)
        LocalDate segunda = LocalDate.now(ZoneId.of("America/Fortaleza")).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        mvc.perform(get("/api/turmas/{id}/chamadas/{data}", turma, segunda))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salva").value(false))
                .andExpect(jsonPath("$.alunos", hasSize(1)))
                .andExpect(jsonPath("$.alunos[0].primeiraHora").value("PRESENTE"));

        enviar(put("/api/turmas/{id}/chamadas/{data}", turma, segunda), """
                {"registros":[{"matriculaId":%d,"primeiraHora":"FALTA","segundaHora":"FALTA"}]}""".formatted(matricula))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salva").value(true))
                .andExpect(jsonPath("$.alunos[0].resultado").value("FALTA"));

        // reabrir a mesma data mostra o que foi salvo
        mvc.perform(get("/api/turmas/{id}/chamadas/{data}", turma, segunda))
                .andExpect(jsonPath("$.salva").value(true))
                .andExpect(jsonPath("$.alunos[0].resultado").value("FALTA"))
                .andExpect(jsonPath("$.alunos[0].atualizadoEm").exists());

        // corrigir: veio só na 2ª hora (presença parcial)
        enviar(put("/api/turmas/{id}/chamadas/{data}", turma, segunda), """
                {"registros":[{"matriculaId":%d,"primeiraHora":"FALTA","segundaHora":"PRESENTE"}]}""".formatted(matricula))
                .andExpect(jsonPath("$.alunos[0].resultado").value("PARCIAL"));

        // RN07: dia errado da semana -> 422
        mvc.perform(get("/api/turmas/{id}/chamadas/{data}", turma, segunda.minusDays(1)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.campos.regra").value("RN07"));

        // histórico em grade
        mvc.perform(get("/api/turmas/{id}/chamadas", turma)
                        .param("de", segunda.minusWeeks(4).toString())
                        .param("ate", segunda.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datas", hasSize(1)))
                .andExpect(jsonPath("$.alunos[0].marcacoes[0].resultado").value("PARCIAL"));

        // cancelar sem motivo -> 400; com motivo libera a vaga e mantém o histórico
        enviar(patch("/api/matriculas/{id}/cancelamento", matricula), """
                {"motivo":""}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.motivo").exists());
        enviar(patch("/api/matriculas/{id}/cancelamento", matricula), """
                {"motivo":"Mudou de cidade"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
        mvc.perform(get("/api/turmas/{id}", turma))
                .andExpect(jsonPath("$.ocupacao").value(0));
        mvc.perform(get("/api/turmas/{id}/chamadas", turma)
                        .param("de", segunda.minusWeeks(4).toString())
                        .param("ate", segunda.toString()))
                .andExpect(jsonPath("$.alunos", hasSize(1)))
                .andExpect(jsonPath("$.alunos[0].statusMatricula").value("CANCELADA"))
                .andExpect(jsonPath("$.alunos[0].marcacoes[0].resultado").value("PARCIAL"));

        // 404
        mvc.perform(get("/api/turmas/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
