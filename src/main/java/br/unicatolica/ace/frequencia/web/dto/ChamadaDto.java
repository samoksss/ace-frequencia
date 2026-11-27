package br.unicatolica.ace.frequencia.web.dto;

import br.unicatolica.ace.frequencia.application.usecase.ConsultarHistoricoChamada;
import br.unicatolica.ace.frequencia.application.usecase.RegistrarChamada;
import br.unicatolica.ace.frequencia.domain.model.Aula;
import br.unicatolica.ace.frequencia.domain.model.Matricula;
import br.unicatolica.ace.frequencia.domain.model.Presenca;
import br.unicatolica.ace.frequencia.domain.model.RegistroFrequencia;
import br.unicatolica.ace.frequencia.domain.model.ResultadoAula;
import br.unicatolica.ace.frequencia.domain.model.StatusMatricula;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChamadaDto {

    private ChamadaDto() {
    }

    // ---------- salvar ----------

    public record Requisicao(@NotNull(message = "Envie a lista de registros.") List<@Valid Marcacao> registros) {

        public List<RegistrarChamada.Marcacao> paraComando() {
            return registros.stream()
                    .map(r -> new RegistrarChamada.Marcacao(r.matriculaId(), r.primeiraHora(), r.segundaHora()))
                    .toList();
        }
    }

    public record Marcacao(
            @Schema(example = "1") @NotNull(message = "Informe a matrícula.") Long matriculaId,
            @Schema(example = "PRESENTE") @NotNull(message = "Marque a 1ª hora.") Presenca primeiraHora,
            @Schema(example = "FALTA") @NotNull(message = "Marque a 2ª hora.") Presenca segundaHora) {
    }

    // ---------- chamada do dia ----------

    public record Resposta(Long turmaId, String turmaNome, LocalDate data,
                           @Schema(description = "true se a chamada desta data já foi salva") boolean salva,
                           List<Linha> alunos) {

        public static Resposta de(Aula aula) {
            List<Linha> linhas = aula.getRegistros().stream()
                    .map(Linha::de)
                    .sorted(Comparator.comparing(Linha::alunoNome))
                    .toList();
            return new Resposta(aula.getTurma().getId(), aula.getTurma().nome(), aula.getData(), aula.getId() != null,
                    linhas);
        }
    }

    public record Linha(Long matriculaId, String alunoNumero, String alunoNome, String cicloNome, String cicloCor,
                        StatusMatricula statusMatricula, Presenca primeiraHora, Presenca segundaHora,
                        ResultadoAula resultado, LocalDateTime atualizadoEm) {

        static Linha de(RegistroFrequencia r) {
            Matricula m = r.getMatricula();
            return new Linha(m.getId(), m.getAluno().getNumeroMatricula().valor(), m.getAluno().getNome(),
                    m.getCiclo().getNome(), m.getCiclo().getCorHex(), m.getStatus(), r.getPrimeiraHora(),
                    r.getSegundaHora(), r.resultado(), r.getAtualizadoEm());
        }
    }

    // ---------- histórico em grade (parecido com a planilha) ----------

    public record Historico(Long turmaId, String turmaNome, LocalDate de, LocalDate ate,
                            @Schema(description = "Colunas da grade: datas com chamada lançada") List<LocalDate> datas,
                            List<LinhaHistorico> alunos) {

        public static Historico de(ConsultarHistoricoChamada.Historico h) {
            List<LocalDate> datas = h.aulas().stream().map(Aula::getData).toList();
            Map<Matricula, LinhaHistorico.Acumulador> porMatricula = new LinkedHashMap<>();
            // alunos ativos aparecem mesmo sem registro; cancelados aparecem se tiverem registro no período
            h.turma().matriculasAtivas().forEach(m -> porMatricula.put(m, new LinhaHistorico.Acumulador(m, datas.size())));
            for (int i = 0; i < h.aulas().size(); i++) {
                for (RegistroFrequencia r : h.aulas().get(i).getRegistros()) {
                    porMatricula.computeIfAbsent(r.getMatricula(), m -> new LinhaHistorico.Acumulador(m, datas.size()))
                            .marcar(i, r);
                }
            }
            List<LinhaHistorico> linhas = porMatricula.values().stream()
                    .map(LinhaHistorico.Acumulador::linha)
                    .sorted(Comparator.comparing(LinhaHistorico::alunoNome))
                    .toList();
            return new Historico(h.turma().getId(), h.turma().nome(), h.de(), h.ate(), datas, linhas);
        }
    }

    public record LinhaHistorico(Long matriculaId, String alunoNumero, String alunoNome, StatusMatricula statusMatricula,
                                 String cicloCor,
                                 @Schema(description = "Uma posição por data da grade; null = sem registro") List<Celula> marcacoes,
                                 int faltas) {

        static final class Acumulador {
            private final Matricula matricula;
            private final List<Celula> celulas;

            Acumulador(Matricula matricula, int colunas) {
                this.matricula = matricula;
                this.celulas = new ArrayList<>(java.util.Collections.nCopies(colunas, (Celula) null));
            }

            void marcar(int coluna, RegistroFrequencia r) {
                celulas.set(coluna, new Celula(r.getPrimeiraHora(), r.getSegundaHora(), r.resultado()));
            }

            LinhaHistorico linha() {
                int faltas = (int) celulas.stream().filter(c -> c != null && c.resultado() == ResultadoAula.FALTA).count();
                return new LinhaHistorico(matricula.getId(), matricula.getAluno().getNumeroMatricula().valor(),
                        matricula.getAluno().getNome(), matricula.getStatus(), matricula.getCiclo().getCorHex(),
                        celulas, faltas);
            }
        }
    }

    public record Celula(Presenca primeiraHora, Presenca segundaHora, ResultadoAula resultado) {
    }
}
