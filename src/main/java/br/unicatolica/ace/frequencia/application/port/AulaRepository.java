package br.unicatolica.ace.frequencia.application.port;

import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.Turma;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Porta do agregado Aula. Registros nunca são apagados (RN07). */
public interface AulaRepository {

    /** @param turma turma completa; os registros são ligados às matrículas dela */
    Optional<Aula> buscar(Turma turma, LocalDate data);

    /** Aulas da turma no período (datas inclusivas), em ordem de data. */
    List<Aula> listar(Turma turma, LocalDate de, LocalDate ate);

    /** Cria a aula se for nova e grava/atualiza todos os registros. */
    Aula salvar(Aula aula);
}
