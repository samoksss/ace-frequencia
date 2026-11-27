package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.CancelarMatricula;
import br.unicatolica.ace.frequencia.application.usecase.MatricularAluno;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testa só a camada web: o caso de uso é um mock. */
@WebMvcTest(MatriculaController.class)
class MatriculaControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    MatricularAluno matricularAluno;

    @MockitoBean
    CancelarMatricula cancelarMatricula;

    @Test
    @DisplayName("regra de negócio violada vira HTTP 422 com o corpo padrão de erro")
    void regraVioladaRetorna422() throws Exception {
        when(matricularAluno.executar(any())).thenThrow(new RegraNegocioException("RN02",
                "Turma Segunda 19-21 ja tem 5 alunos de cursos avancados (limite de computadores)."));

        mvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoNumero":"9305","turmaId":1,"cursoId":3,"cicloId":1}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensagem", containsString("limite de computadores")))
                .andExpect(jsonPath("$.campos.regra").value("RN02"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("campo obrigatório faltando vira HTTP 400 com o campo listado, sem chamar o caso de uso")
    void validacaoRetorna400() throws Exception {
        mvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoNumero":"9305","cursoId":3,"cicloId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.turmaId").exists());
        verifyNoInteractions(matricularAluno);
    }
}
