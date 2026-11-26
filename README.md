# Sistema Web de Controle de Frequência — New Center Informática

Atividade Curricular de Extensão (ACE 8) · Análise e Projeto de Sistemas II · Sistemas de Informação · UniCatólica · 2026.2

**Equipe:** Marcos Felipe Arruda Lima · Samuel Brito da Silva · Maria Leticia Pimentel Carlos
**Orientador:** Prof. Wagner Luiz Braga Bezerra

> Projeto em desenvolvimento. Este README é completado ao longo das sprints (ver `DIARIO.md`).

## O problema

Na New Center, a chamada é uma planilha impressa e marcada à mão. A cada dois meses a planilha é refeita do zero e o histórico digital se perde. Para achar quem está faltando, a recepção olha o papel, procura o telefone na planilha e manda mensagem aluno por aluno. Cada turma tem no máximo 20 alunos, e só 5 deles podem ser de cursos avançados, porque só há 5 computadores para esses cursos em cada horário.

## A solução

API REST em Java 21 + Spring Boot 3 e um front-end web leve (HTML + JS + Bootstrap) servido pela própria aplicação:

- **Painel de Faltosos** (tela inicial): 2 faltas seguidas = ATENÇÃO, 3 ou mais = CRÍTICO, com o telefone em destaque e um botão para abrir o WhatsApp
- **Chamada digital** por turma e data (1ª e 2ª hora), com histórico permanente
- **Matrícula com regras de negócio**: capacidade, cota de avançados, limite por aluno e ciclo encerrado

## Como rodar

Pré-requisitos: Java 21 e Maven.

```bash
mvn test              # roda os testes
mvn spring-boot:run   # sobe em http://localhost:8080
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/frequencia`, usuário `sa`)

## Regras de negócio

| Código | Regra |
|---|---|
| RN01 | Máximo de 20 matrículas ATIVAS por turma |
| RN02 | Dessas 20, no máximo 5 em cursos AVANÇADOS (qualquer tipo) |
| RN03 | Faltoso continua ocupando a vaga; só o cancelamento libera |
| RN04 | 1 matrícula ativa por aluno; curso rápido permite 2, em dias diferentes |
| RN05 | Número de matrícula único |
| RN06 | Ciclo que não aceita matrículas (ex.: Laranja) bloqueia novas matrículas |
| RN07 | Chamada vinculada a turma + data (dia da semana certo, sem data futura); nunca apagada |
| RN08 | Aula = 2 marcações; falta = ausente nas duas horas |
| RN09 | Alerta por faltas consecutivas: 2 = ATENÇÃO, 3+ = CRÍTICO (configurável) |
| RN10 | Cancelamento exige motivo e mantém o histórico |
| RN11 | Duração prevista: regular 12 meses, rápido 6 meses |

## Arquitetura

Clean Architecture simplificada. As dependências apontam para dentro: `web → application → domain`, e a `infrastructure` implementa as portas da `application`.

```
br.unicatolica.ace.frequencia
├── domain            regras puras, sem Spring e sem JPA
│   ├── model         Aluno, Curso, Ciclo, Turma, Matricula, Aula, RegistroFrequencia, Contato
│   ├── vo            NumeroMatricula, Telefone, Horario
│   ├── regra         RegraMatricula + implementações, ValidadorMatricula
│   ├── service       CalculadoraFaltas, CriterioAlerta
│   └── exception     RegraNegocioException (422), DadoInvalidoException (400)
├── application       casos de uso + portas (interfaces de repositório)
├── infrastructure    JPA/H2, adaptadores das portas, dados fictícios
└── web               controllers REST, DTOs, tratamento de erros
```

Diagramas em `docs/uml/` (casos de uso e classes; sequência na sprint de fechamento).

### Padrões de projeto

| Padrão | Onde | Por quê |
|---|---|---|
| **Strategy** (GoF) | `RegraMatricula`: `CapacidadeMaximaRegra`, `CotaAvancadoRegra`, `LimiteMatriculasPorAlunoRegra`, `CicloAceitaMatriculaRegra` | Cada regra fica isolada; uma regra nova é uma classe nova (OCP) |
| **Composite** (GoF) | `ValidadorMatricula` agrega as regras e também é uma `RegraMatricula` | O caso de uso chama um único validador |
| **Strategy** (GoF) | `CriterioAlerta`: `FaltasConsecutivasCriterio`, `TotalFaltasCriterio` | O critério de faltoso pode mudar sem mexer no painel |
| **Factory Method** (GoF) | `Aula.abrir(turma, data, hoje)` | Centraliza a criação dos registros e a validação da data (RN07) |
| **Adapter** (GoF) | Repositórios JPA implementando as portas | Domínio independente do banco |
| **Information Expert** (GRASP) | `Turma` conta vagas e avançados; `Matricula` sabe se está ativa | O comportamento fica junto dos dados |
| **Creator** (GRASP) | `Turma` cria `Matricula`; `Aula` cria `RegistroFrequencia` | Quem agrega, cria |
| **Controller** (GRASP) | Um caso de uso por operação do sistema | Sem regra de negócio no controller REST |
| **Pure Fabrication / Indirection** (GRASP) | `CalculadoraFaltas`, portas de repositório | Coesão alta e acoplamento baixo |
| **Polymorphism** (GRASP) | Implementações de `RegraMatricula` | Elimina cadeias de `if/else` |

## Dados

Este repositório usa **somente dados fictícios** (LGPD). A planilha real da escola nunca é versionada.
