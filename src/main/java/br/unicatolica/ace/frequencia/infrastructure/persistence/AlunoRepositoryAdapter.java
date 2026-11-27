package br.unicatolica.ace.frequencia.infrastructure.persistence;

import br.unicatolica.ace.frequencia.application.port.AlunoRepository;
import br.unicatolica.ace.frequencia.domain.model.Aluno;
import br.unicatolica.ace.frequencia.domain.vo.NumeroMatricula;
import br.unicatolica.ace.frequencia.infrastructure.persistence.jpa.AlunoJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** GoF Adapter: implementa a porta AlunoRepository com Spring Data JPA. */
@Repository
@Transactional(readOnly = true)
public class AlunoRepositoryAdapter implements AlunoRepository {

    private final AlunoJpa jpa;

    public AlunoRepositoryAdapter(AlunoJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Aluno> buscar(NumeroMatricula numero) {
        return jpa.findById(numero.valor()).map(Mapeador::aluno);
    }

    @Override
    public boolean existe(NumeroMatricula numero) {
        return jpa.existsById(numero.valor());
    }

    @Override
    public List<Aluno> pesquisar(String texto) {
        var encontrados = texto.isBlank()
                ? jpa.findAllByOrderByNomeAsc()
                : jpa.findByNomeContainingIgnoreCaseOrNumeroMatriculaContainingIgnoreCaseOrderByNomeAsc(texto, texto);
        return encontrados.stream().map(Mapeador::aluno).toList();
    }

    @Override
    @Transactional
    public Aluno salvar(Aluno aluno) {
        return Mapeador.aluno(jpa.save(Mapeador.aluno(aluno)));
    }
}
