package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.ConsultarHistoricoChamada;
import br.unicatolica.ace.frequencia.application.usecase.RegistrarChamada;
import br.unicatolica.ace.frequencia.web.dto.ChamadaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/turmas/{turmaId}/chamadas")
@Tag(name = "Chamada", description = "Chamada digital por turma e data (1ª e 2ª hora) e histórico")
public class ChamadaController {

    private final RegistrarChamada registrarChamada;
    private final ConsultarHistoricoChamada consultarHistorico;

    public ChamadaController(RegistrarChamada registrarChamada, ConsultarHistoricoChamada consultarHistorico) {
        this.registrarChamada = registrarChamada;
        this.consultarHistorico = consultarHistorico;
    }

    @GetMapping("/{data}")
    @Operation(summary = "Chamada do dia (pré-preenchida com os alunos ativos, todos PRESENTE)",
            description = "A data deve cair no dia da semana da turma e não pode ser futura (RN07).")
    public ChamadaDto.Resposta abrir(@PathVariable Long turmaId,
                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ChamadaDto.Resposta.de(registrarChamada.abrir(turmaId, data));
    }

    @PutMapping("/{data}")
    @Operation(summary = "Salvar ou corrigir a chamada do dia")
    public ChamadaDto.Resposta salvar(@PathVariable Long turmaId,
                                      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                      @Valid @RequestBody ChamadaDto.Requisicao r) {
        return ChamadaDto.Resposta.de(registrarChamada.salvar(turmaId, data, r.paraComando()));
    }

    @GetMapping
    @Operation(summary = "Histórico em grade (como a planilha), para qualquer período")
    public ChamadaDto.Historico historico(@PathVariable Long turmaId,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return ChamadaDto.Historico.de(consultarHistorico.executar(turmaId, de, ate));
    }
}
