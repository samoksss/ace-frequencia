package br.unicatolica.ace.frequencia.domain.model;

/**
 * Curso que o aluno faz (Básico, Básico Rápido, Avançado I – Empresarial, Avançado Kids...).
 * A categoria define se ocupa computador de avançado (RN02); a flag rápido muda duração (RN11)
 * e permite 2 matrículas (RN04).
 */
public class Curso {

    static final int DURACAO_REGULAR_MESES = 12;
    static final int DURACAO_RAPIDO_MESES = 6;

    private final Long id;
    private final String nome;
    private final CategoriaCurso categoria;
    private final boolean rapido;

    public Curso(Long id, String nome, CategoriaCurso categoria, boolean rapido) {
        this.id = id;
        this.nome = Textos.obrigatorio(nome, "nome", "Informe o nome do curso.");
        this.categoria = Textos.obrigatorio(categoria, "categoria", "Informe a categoria do curso (BASICO ou AVANCADO).");
        this.rapido = rapido;
    }

    public boolean isAvancado() {
        return categoria == CategoriaCurso.AVANCADO;
    }

    /** RN11: curso regular 12 meses, curso rápido 6 meses. */
    public int duracaoMeses() {
        return rapido ? DURACAO_RAPIDO_MESES : DURACAO_REGULAR_MESES;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CategoriaCurso getCategoria() {
        return categoria;
    }

    public boolean isRapido() {
        return rapido;
    }

    @Override
    public String toString() {
        return nome;
    }
}
