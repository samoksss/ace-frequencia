package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.CancelarMatricula;
import br.unicatolica.ace.frequencia.application.usecase.MatricularAluno;
import br.unicatolica.ace.frequencia.web.dto.MatriculaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matriculas")
@Tag(name = "Matrículas", description = "Matricular (regras RN01–RN06, 422 se violar) e cancelar com motivo")
public class MatriculaController {

    private final MatricularAluno matricularAluno;
    private final CancelarMatricula cancelarMatricula;

    public MatriculaController(MatricularAluno matricularAluno, CancelarMatricula cancelarMatricula) {
        this.matricularAluno = matricularAluno;
        this.cancelarMatricula = cancelarMatricula;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Matricular aluno numa turma",
            description = "Bloqueia com 422: turma lotada (RN01), 6º avançado (RN02), 2ª matrícula de curso regular (RN04), ciclo encerrando (RN06).")
    public MatriculaDto.Resposta matricular(@Valid @RequestBody MatriculaDto.Requisicao r) {
        return MatriculaDto.Resposta.de(matricularAluno.executar(new MatricularAluno.Comando(
                r.alunoNumero(), r.turmaId(), r.cursoId(), r.cicloId(), r.dataInicio())));
    }

    @PatchMapping("/{id}/cancelamento")
    @Operation(summary = "Cancelar matrícula com motivo (libera a vaga, mantém o histórico)")
    public MatriculaDto.Resposta cancelar(@PathVariable Long id, @Valid @RequestBody MatriculaDto.Cancelamento r) {
        return MatriculaDto.Resposta.de(cancelarMatricula.executar(id, r.motivo()));
    }
}
