package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.usecase.GerenciarCursos;
import br.unicatolica.ace.frequencia.web.dto.CursoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Cursos e categoria (BASICO/AVANCADO)")
public class CursoController {

    private final GerenciarCursos gerenciarCursos;

    public CursoController(GerenciarCursos gerenciarCursos) {
        this.gerenciarCursos = gerenciarCursos;
    }

    @GetMapping
    @Operation(summary = "Listar cursos")
    public List<CursoDto.Resposta> listar() {
        return gerenciarCursos.listar().stream().map(CursoDto.Resposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar curso")
    public CursoDto.Resposta criar(@Valid @RequestBody CursoDto.Requisicao r) {
        return CursoDto.Resposta.de(gerenciarCursos.cadastrar(
                new GerenciarCursos.Comando(r.nome(), r.categoria(), r.rapido())));
    }
}
