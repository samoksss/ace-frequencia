package br.unicatolica.ace.frequencia.domain.model;

import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Uma data específica de uma turma (ex.: Turma Segunda 09–11 em 07/12/2026).
 * Raiz do agregado que guarda os registros de frequência.
 * GoF Factory Method: {@link #abrir} valida a data (RN07) e cria os registros.
 * GRASP Creator: a Aula cria os RegistroFrequencia.
 */
public class Aula {

    /**
     * Marcação inicial de uma chamada nova: o professor marca quem está presente
     * (ou usa "todos presentes"). Decisão de interface registrada no DIARIO.md.
     */
    static final Presenca MARCACAO_INICIAL = Presenca.FALTA;

    private final Long id;
    private final Turma turma;
    private final LocalDate data;
    private final List<RegistroFrequencia> registros = new ArrayList<>();

    private Aula(Long id, Turma turma, LocalDate data) {
        this.id = id;
        this.turma = turma;
        this.data = data;
    }

    /**
     * Abre a chamada da turma na data, com um registro para cada matrícula ativa.
     * RN07: a data deve cair no dia da semana da turma e não pode ser futura.
     */
    public static Aula abrir(Turma turma, LocalDate data, LocalDate hoje) {
        Textos.obrigatorio(turma, "turma", "Informe a turma.");
        Textos.obrigatorio(data, "data", "Informe a data da aula.");
        validarData(turma, data, hoje);
        Aula aula = new Aula(null, turma, data);
        aula.incluirMatriculasAtivasSemRegistro();
        return aula;
    }

    /** Reconstitui uma aula já salva (persistência). Não revalida a data: o histórico é imutável. */
    public static Aula existente(Long id, Turma turma, LocalDate data, List<RegistroFrequencia> registros) {
        Aula aula = new Aula(id, turma, data);
        aula.registros.addAll(registros);
        return aula;
    }

    private static void validarData(Turma turma, LocalDate data, LocalDate hoje) {
        if (data.getDayOfWeek() != turma.getDiaSemana()) {
            throw new RegraNegocioException("RN07", "A turma %s só tem aula na %s; a data %s é %s.".formatted(
                    turma.nome(), DiasDaSemana.nome(turma.getDiaSemana()).toLowerCase(),
                    Datas.formatar(data), DiasDaSemana.nome(data.getDayOfWeek()).toLowerCase()));
        }
        if (data.isAfter(hoje)) {
            throw new RegraNegocioException("RN07", "Não é possível lançar chamada de data futura (" + Datas.formatar(data) + ").");
        }
    }

    /**
     * Inclui registros para matrículas ativas que ainda não estão na chamada
     * (ex.: aluno matriculado depois que a aula foi lançada). Nunca remove registros.
     */
    public void incluirMatriculasAtivasSemRegistro() {
        for (Matricula matricula : turma.matriculasAtivas()) {
            if (registroDe(matricula).isEmpty()) {
                registros.add(new RegistroFrequencia(matricula, MARCACAO_INICIAL, MARCACAO_INICIAL, null));
            }
        }
    }

    /** Lança ou corrige a frequência de uma matrícula nesta aula. */
    public RegistroFrequencia registrar(Matricula matricula, Presenca primeiraHora, Presenca segundaHora, LocalDateTime agora) {
        RegistroFrequencia registro = registroDe(matricula).orElseGet(() -> novoRegistro(matricula));
        registro.registrar(primeiraHora, segundaHora, agora);
        return registro;
    }

    /** "Todos presentes": marca as duas horas como PRESENTE para todos da chamada. */
    public void marcarTodosPresentes(LocalDateTime agora) {
        registros.forEach(r -> r.registrar(Presenca.PRESENTE, Presenca.PRESENTE, agora));
    }

    private RegistroFrequencia novoRegistro(Matricula matricula) {
        if (!matricula.getTurma().equals(turma)) {
            throw new DadoInvalidoException("matricula", matricula.getAluno() + " não é da turma " + turma.nome() + ".");
        }
        if (!matricula.isAtiva()) {
            throw new RegraNegocioException("RN07", "A matrícula de " + matricula.getAluno() + " não está ativa.");
        }
        RegistroFrequencia registro = new RegistroFrequencia(matricula, MARCACAO_INICIAL, MARCACAO_INICIAL, null);
        registros.add(registro);
        return registro;
    }

    public Optional<RegistroFrequencia> registroDe(Matricula matricula) {
        return registros.stream().filter(r -> r.getMatricula().equals(matricula)).findFirst();
    }

    public Long getId() {
        return id;
    }

    public Turma getTurma() {
        return turma;
    }

    public LocalDate getData() {
        return data;
    }

    public List<RegistroFrequencia> getRegistros() {
        return Collections.unmodifiableList(registros);
    }
}
