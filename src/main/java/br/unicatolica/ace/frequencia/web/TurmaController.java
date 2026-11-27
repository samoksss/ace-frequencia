package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.CadastrarTurma;
import br.unicatolica.ace.frequencia.application.usecase.ConsultarTurmas;
import br.unicatolica.ace.frequencia.domain.model.Turno;
import br.unicatolica.ace.frequencia.web.dto.MatriculaDto;
import br.unicatolica.ace.frequencia.web.dto.TurmaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@Tag(name = "Turmas", description = "Horários fixos da semana, com ocupação (RN01/RN02)")
public class TurmaController {

    private final CadastrarTurma cadastrarTurma;
    private final ConsultarTurmas consultarTurmas;

    public TurmaController(CadastrarTurma cadastrarTurma, ConsultarTurmas consultarTurmas) {
        this.cadastrarTurma = cadastrarTurma;
        this.consultarTurmas = consultarTurmas;
    }

    @GetMapping
    @Operation(summary = "Listar turmas (filtros opcionais: turno, diaSemana)")
    public List<TurmaDto.Resposta> listar(@RequestParam(required = false) Turno turno,
                                          @RequestParam(required = false) DayOfWeek diaSemana) {
        return consultarTurmas.listar(turno, diaSemana).stream().map(TurmaDto.Resposta::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar turma")
    public TurmaDto.Resposta buscar(@PathVariable Long id) {
        return TurmaDto.Resposta.de(consultarTurmas.buscar(id));
    }

    @GetMapping("/{id}/matriculas")
    @Operation(summary = "Alunos ativos da turma")
    public List<MatriculaDto.Resposta> matriculas(@PathVariable Long id) {
        return consultarTurmas.matriculasAtivas(id).stream().map(MatriculaDto.Resposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar turma")
    public TurmaDto.Resposta criar(@Valid @RequestBody TurmaDto.Requisicao r) {
        return TurmaDto.Resposta.de(cadastrarTurma.cadastrar(comando(r)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar dia/horário da turma")
    public TurmaDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody TurmaDto.Requisicao r) {
        return TurmaDto.Resposta.de(cadastrarTurma.atualizar(id, comando(r)));
    }

    private static CadastrarTurma.Comando comando(TurmaDto.Requisicao r) {
        return new CadastrarTurma.Comando(r.diaSemana(), r.horaInicio(), r.horaFim());
    }
}
