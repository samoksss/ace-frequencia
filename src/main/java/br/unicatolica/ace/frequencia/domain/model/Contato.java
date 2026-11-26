package br.unicatolica.ace.frequencia.domain.model;

import java.time.LocalDateTime;

/**
 * Registro de que a recepção falou com o aluno. Substitui o "OK / AVISADO" e o vermelho da planilha.
 */
public class Contato {

    private final Long id;
    private final Matricula matricula;
    private final LocalDateTime dataHora;
    private final CanalContato canal;
    private final ResultadoContato resultado;
    private final String observacao;

    public Contato(Long id, Matricula matricula, LocalDateTime dataHora, CanalContato canal,
                   ResultadoContato resultado, String observacao) {
        this.id = id;
        this.matricula = Textos.obrigatorio(matricula, "matricula", "Informe a matrícula do aluno contatado.");
        this.dataHora = Textos.obrigatorio(dataHora, "dataHora", "Informe a data do contato.");
        this.canal = Textos.obrigatorio(canal, "canal", "Informe o canal (WhatsApp, ligação ou presencial).");
        this.resultado = Textos.obrigatorio(resultado, "resultado", "Informe o resultado do contato.");
        this.observacao = Textos.opcional(observacao);
    }

    public Long getId() {
        return id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public CanalContato getCanal() {
        return canal;
    }

    public ResultadoContato getResultado() {
        return resultado;
    }

    public String getObservacao() {
        return observacao;
    }
}
