package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.GerenciarAlunos;
import br.unicatolica.ace.frequencia.web.dto.AlunoDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Alunos identificados pelo número de matrícula da escola (RN05)")
public class AlunoController {

    private final GerenciarAlunos gerenciarAlunos;

    public AlunoController(GerenciarAlunos gerenciarAlunos) {
        this.gerenciarAlunos = gerenciarAlunos;
    }

    @GetMapping
    @Operation(summary = "Pesquisar alunos por nome ou número de matrícula")
    public List<AlunoDto.Resposta> pesquisar(@RequestParam(required = false) String busca) {
        return gerenciarAlunos.pesquisar(busca).stream().map(AlunoDto.Resposta::de).toList();
    }

    @GetMapping("/{numeroMatricula}")
    @Operation(summary = "Buscar aluno pelo número de matrícula")
    public AlunoDto.Resposta buscar(@PathVariable String numeroMatricula) {
        return AlunoDto.Resposta.de(gerenciarAlunos.buscar(numeroMatricula));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar aluno (409 se o número de matrícula já existir)")
    public AlunoDto.Resposta criar(@Valid @RequestBody AlunoDto.Requisicao r) {
        return AlunoDto.Resposta.de(gerenciarAlunos.cadastrar(
                new GerenciarAlunos.Comando(r.numeroMatricula(), r.nome(), r.telefone(), r.observacao())));
    }

    @PutMapping("/{numeroMatricula}")
    @Operation(summary = "Alterar nome, telefone e observação do aluno")
    public AlunoDto.Resposta atualizar(@PathVariable String numeroMatricula, @Valid @RequestBody AlunoDto.Atualizacao r) {
        return AlunoDto.Resposta.de(gerenciarAlunos.atualizar(numeroMatricula,
                new GerenciarAlunos.Comando(numeroMatricula, r.nome(), r.telefone(), r.observacao())));
    }
}
