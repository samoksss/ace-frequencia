# Diário de desenvolvimento — Sistema Web de Controle de Frequência

Registro curto de cada sessão: o que foi feito, decisões e dificuldades. Serve de base para as seções 3 (etapas) e 5 (reflexão) do relatório da ACE.

---

## 26/11/2026 · Sprint 0: Ideação e modelagem

**Feito:**
- Projeto Spring Boot 3 / Java 21 criado com a estrutura de camadas (domain, application, infrastructure, web) e H2 em arquivo.
- Domínio modelado com a linguagem da New Center: Aluno, Curso, Ciclo (cor), Turma, Matrícula, Aula, Registro de frequência e Contato, além dos value objects NumeroMatricula, Telefone e Horario.
- Regras de matrícula RN01, RN02, RN03, RN04 e RN06 implementadas, cada uma em uma classe (Strategy), reunidas no ValidadorMatricula (Composite). RN07, RN08, RN10 e RN11 ficaram nas entidades. RN09 está na CalculadoraFaltas, com critério plugável.
- 38 testes unitários de domínio (JUnit 5), cobrindo todos os casos da seção 10 (21º aluno, 6º avançado inclusive Kids, curso rápido em 2 turmas, ciclo Laranja, faltas consecutivas, presença parcial, data no dia errado).
- Diagramas UML de casos de uso e de classes (PlantUML).

**Decisões:**
- O domínio **não depende de Spring nem de JPA**: compila e é testado sozinho. A persistência (sprint 1) vai usar entidades JPA separadas na `infrastructure`, mapeadas para o domínio pelos adaptadores.
- Uma chamada nova começa com **todos PRESENTE** e o professor marca só as faltas. A primeira versão começava com todos em FALTA; a equipe (Marcos Felipe) preferiu o contrário, porque é mais rápido no dia a dia.
- A **Recepção também cadastra turmas**, já que as matrículas são feitas lá. O Proprietário usa o mesmo PC da recepção e por isso herda todos os casos de uso dela (generalização no diagrama).
- As regras são avaliadas na ordem ciclo → limite por aluno → capacidade → cota de avançados. A primeira violação interrompe a matrícula com uma mensagem pronta para a recepção.

**Dificuldades:**
- Parte do código foi escrita com apoio de IA (Claude) num ambiente sem acesso ao Maven Central nem ao PlantUML. Lá, o domínio e os testes foram conferidos só com o compilador Java, e o build Maven e a geração dos PNGs ficaram para o MacBook.
- No MacBook foi preciso instalar o ambiente do zero (Homebrew, Java 21, Maven, PlantUML). O Maven veio com um Java 27 como dependência e passou a usá-lo no lugar do 21; resolvemos fixando o JAVA_HOME no Java 21.
- Repositório publicado no GitHub (público): https://github.com/samoksss/ace-frequencia. Os testes rodaram no Mac: 39 testes, BUILD SUCCESS.
