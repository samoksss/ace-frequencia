package br.unicatolica.ace.frequencia.infrastructure.config;

import br.unicatolica.ace.frequencia.application.port.AlunoRepository;
import br.unicatolica.ace.frequencia.application.port.AulaRepository;
import br.unicatolica.ace.frequencia.application.port.CicloRepository;
import br.unicatolica.ace.frequencia.application.port.CursoRepository;
import br.unicatolica.ace.frequencia.application.port.MatriculaRepository;
import br.unicatolica.ace.frequencia.application.port.TurmaRepository;
import br.unicatolica.ace.frequencia.application.usecase.CadastrarTurma;
import br.unicatolica.ace.frequencia.application.usecase.CancelarMatricula;
import br.unicatolica.ace.frequencia.application.usecase.ConsultarHistoricoChamada;
import br.unicatolica.ace.frequencia.application.usecase.ConsultarTurmas;
import br.unicatolica.ace.frequencia.application.usecase.GerenciarAlunos;
import br.unicatolica.ace.frequencia.application.usecase.GerenciarCiclos;
import br.unicatolica.ace.frequencia.application.usecase.GerenciarCursos;
import br.unicatolica.ace.frequencia.application.usecase.MatricularAluno;
import br.unicatolica.ace.frequencia.application.usecase.RegistrarChamada;
import br.unicatolica.ace.frequencia.domain.regra.RegraMatricula;
import br.unicatolica.ace.frequencia.domain.regra.ValidadorMatricula;
import br.unicatolica.ace.frequencia.domain.service.CalculadoraFaltas;
import br.unicatolica.ace.frequencia.domain.service.FaltasConsecutivasCriterio;
import br.unicatolica.ace.frequencia.domain.service.LimitesAlerta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Monta os casos de uso (que não conhecem o Spring) com as implementações das portas.
 * Inversão de dependência (o D do SOLID): a aplicação depende de interfaces; aqui escolhemos as implementações.
 */
@Configuration
public class CasosDeUsoConfig {

    /** Quixeramobim/CE: horário de Fortaleza. */
    @Bean
    public Clock relogio() {
        return Clock.system(ZoneId.of("America/Fortaleza"));
    }

    @Bean
    public RegraMatricula validadorMatricula() {
        return ValidadorMatricula.padrao();
    }

    /** RN09: limites lidos do application.yml (padrão 2 e 3). */
    @Bean
    public CalculadoraFaltas calculadoraFaltas(@Value("${frequencia.alerta.atencao:2}") int atencao,
                                               @Value("${frequencia.alerta.critico:3}") int critico) {
        return new CalculadoraFaltas(new LimitesAlerta(atencao, critico), new FaltasConsecutivasCriterio());
    }

    @Bean
    public GerenciarCursos gerenciarCursos(CursoRepository cursos) {
        return new GerenciarCursos(cursos);
    }

    @Bean
    public GerenciarCiclos gerenciarCiclos(CicloRepository ciclos) {
        return new GerenciarCiclos(ciclos);
    }

    @Bean
    public CadastrarTurma cadastrarTurma(TurmaRepository turmas) {
        return new CadastrarTurma(turmas);
    }

    @Bean
    public ConsultarTurmas consultarTurmas(TurmaRepository turmas) {
        return new ConsultarTurmas(turmas);
    }

    @Bean
    public GerenciarAlunos gerenciarAlunos(AlunoRepository alunos) {
        return new GerenciarAlunos(alunos);
    }

    @Bean
    public MatricularAluno matricularAluno(AlunoRepository alunos, TurmaRepository turmas, CursoRepository cursos,
                                           CicloRepository ciclos, MatriculaRepository matriculas,
                                           RegraMatricula validadorMatricula, Clock relogio) {
        return new MatricularAluno(alunos, turmas, cursos, ciclos, matriculas, validadorMatricula, relogio);
    }

    @Bean
    public CancelarMatricula cancelarMatricula(MatriculaRepository matriculas, Clock relogio) {
        return new CancelarMatricula(matriculas, relogio);
    }

    @Bean
    public RegistrarChamada registrarChamada(TurmaRepository turmas, AulaRepository aulas, Clock relogio) {
        return new RegistrarChamada(turmas, aulas, relogio);
    }

    @Bean
    public ConsultarHistoricoChamada consultarHistoricoChamada(TurmaRepository turmas, AulaRepository aulas) {
        return new ConsultarHistoricoChamada(turmas, aulas);
    }
}
