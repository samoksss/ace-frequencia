package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.GerenciarCiclos;
import br.unicatolica.ace.frequencia.web.dto.CicloDto;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ciclos")
@Tag(name = "Ciclos", description = "Ciclos de início por cor (Amarela, Azul, Laranja...)")
public class CicloController {

    private final GerenciarCiclos gerenciarCiclos;

    public CicloController(GerenciarCiclos gerenciarCiclos) {
        this.gerenciarCiclos = gerenciarCiclos;
    }

    @GetMapping
    @Operation(summary = "Listar ciclos")
    public List<CicloDto.Resposta> listar() {
        return gerenciarCiclos.listar().stream().map(CicloDto.Resposta::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ciclo")
    public CicloDto.Resposta buscar(@PathVariable Long id) {
        return CicloDto.Resposta.de(gerenciarCiclos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar ciclo")
    public CicloDto.Resposta criar(@Valid @RequestBody CicloDto.Requisicao r) {
        return CicloDto.Resposta.de(gerenciarCiclos.cadastrar(comando(r)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar ciclo (ex.: marcar que não aceita mais matrículas)")
    public CicloDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody CicloDto.Requisicao r) {
        return CicloDto.Resposta.de(gerenciarCiclos.atualizar(id, comando(r)));
    }

    private static GerenciarCiclos.Comando comando(CicloDto.Requisicao r) {
        return new GerenciarCiclos.Comando(r.nome(), r.corHex(), r.dataInicio(), r.dataPrevistaTermino(),
                r.aceitaMatriculas());
    }
}
