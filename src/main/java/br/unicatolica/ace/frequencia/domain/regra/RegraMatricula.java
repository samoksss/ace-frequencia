package br.unicatolica.ace.frequencia.domain.regra;

import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;

/**
 * GoF Strategy: cada regra de matrícula é uma implementação isolada.
 * Nova regra = nova classe, sem mexer nas existentes (Open/Closed).
 * GRASP Polymorphism: elimina a cadeia de if/else de regras.
 */
public interface RegraMatricula {

    /**
     * @throws RegraNegocioException se a matrícula violar a regra (mensagem pronta para a recepção)
     */
    void validar(SolicitacaoMatricula solicitacao);
}
