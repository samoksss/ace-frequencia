package br.unicatolica.ace.frequencia.domain.model;

import java.time.LocalDateTime;

/**
 * Frequência de uma matrícula numa aula: 1ª hora e 2ª hora (RN08).
 * Nunca é apagado; pode ser corrigido, guardando quando foi atualizado (RN07).
 */
public class RegistroFrequencia {

    private final Matricula matricula;
    private Presenca primeiraHora;
    private Presenca segundaHora;
    private LocalDateTime atualizadoEm;

    public RegistroFrequencia(Matricula matricula, Presenca primeiraHora, Presenca segundaHora, LocalDateTime atualizadoEm) {
        this.matricula = Textos.obrigatorio(matricula, "matricula", "Informe a matrícula.");
        this.primeiraHora = Textos.obrigatorio(primeiraHora, "primeiraHora", "Marque a 1ª hora.");
        this.segundaHora = Textos.obrigatorio(segundaHora, "segundaHora", "Marque a 2ª hora.");
        this.atualizadoEm = atualizadoEm;
    }

    /** Lança ou corrige as duas marcações. */
    public void registrar(Presenca primeiraHora, Presenca segundaHora, LocalDateTime agora) {
        this.primeiraHora = Textos.obrigatorio(primeiraHora, "primeiraHora", "Marque a 1ª hora.");
        this.segundaHora = Textos.obrigatorio(segundaHora, "segundaHora", "Marque a 2ª hora.");
        this.atualizadoEm = Textos.obrigatorio(agora, "atualizadoEm", "Informe o momento do registro.");
    }

    public ResultadoAula resultado() {
        return ResultadoAula.de(primeiraHora, segundaHora);
    }

    /** RN08: falta na aula = ausente nas duas horas. */
    public boolean isFalta() {
        return resultado() == ResultadoAula.FALTA;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public Presenca getPrimeiraHora() {
        return primeiraHora;
    }

    public Presenca getSegundaHora() {
        return segundaHora;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
