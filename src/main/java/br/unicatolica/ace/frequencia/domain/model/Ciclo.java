package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;

import java.time.LocalDate;

/**
 * Ciclo de início identificado por cor, como nas linhas da planilha (Amarela, Verde, Azul, Branca, Laranja).
 * Vermelho NÃO é ciclo: na planilha é marcação de situação, que no sistema virou o alerta + Contato.
 * Um ciclo encerrando (ex.: Laranja) não aceita novas matrículas (RN06).
 */
public class Ciclo {

    private static final String FORMATO_COR = "#[0-9A-Fa-f]{6}";

    private final Long id;
    private String nome;
    private String corHex;
    private LocalDate dataInicio;
    private LocalDate dataPrevistaTermino;
    private boolean aceitaMatriculas;

    public Ciclo(Long id, String nome, String corHex, LocalDate dataInicio, LocalDate dataPrevistaTermino,
                 boolean aceitaMatriculas) {
        this.id = id;
        atualizar(nome, corHex, dataInicio, dataPrevistaTermino, aceitaMatriculas);
    }

    public final void atualizar(String nome, String corHex, LocalDate dataInicio, LocalDate dataPrevistaTermino,
                                boolean aceitaMatriculas) {
        this.nome = Textos.obrigatorio(nome, "nome", "Informe o nome do ciclo (ex.: Amarela).");
        String cor = Textos.obrigatorio(corHex, "corHex", "Informe a cor do ciclo (ex.: #FFD600).");
        if (!cor.matches(FORMATO_COR)) {
            throw new DadoInvalidoException("corHex", "Cor inválida: use o formato #RRGGBB (ex.: #FFD600).");
        }
        this.corHex = cor.toUpperCase();
        this.dataInicio = Textos.obrigatorio(dataInicio, "dataInicio", "Informe a data de início do ciclo.");
        this.dataPrevistaTermino = Textos.obrigatorio(dataPrevistaTermino, "dataPrevistaTermino",
                "Informe a previsão de término do ciclo.");
        if (dataPrevistaTermino.isBefore(dataInicio)) {
            throw new DadoInvalidoException("dataPrevistaTermino", "A previsão de término não pode ser antes do início do ciclo.");
        }
        this.aceitaMatriculas = aceitaMatriculas;
    }

    /** Marca o ciclo como encerrando: não recebe mais matrículas (RN06). */
    public void encerrarMatriculas() {
        this.aceitaMatriculas = false;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCorHex() {
        return corHex;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public boolean isAceitaMatriculas() {
        return aceitaMatriculas;
    }

    @Override
    public String toString() {
        return nome;
    }
}
