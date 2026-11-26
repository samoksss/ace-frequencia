package br.unicatolica.ace.frequencia.domain.regra;

import java.util.List;

/**
 * GoF Composite: agrega várias regras e é, ele mesmo, uma RegraMatricula.
 * O caso de uso chama um único validador; para incluir uma regra, basta adicioná-la à lista.
 * As regras rodam na ordem da lista e a primeira violação interrompe a matrícula.
 */
public class ValidadorMatricula implements RegraMatricula {

    private final List<RegraMatricula> regras;

    public ValidadorMatricula(List<RegraMatricula> regras) {
        this.regras = List.copyOf(regras);
    }

    /** Regras confirmadas com a New Center: RN06, RN04, RN01 (com RN03) e RN02. */
    public static ValidadorMatricula padrao() {
        return new ValidadorMatricula(List.of(
                new CicloAceitaMatriculaRegra(),
                new LimiteMatriculasPorAlunoRegra(),
                new CapacidadeMaximaRegra(),
                new CotaAvancadoRegra()));
    }

    @Override
    public void validar(SolicitacaoMatricula solicitacao) {
        regras.forEach(regra -> regra.validar(solicitacao));
    }

    public List<RegraMatricula> getRegras() {
        return regras;
    }
}
