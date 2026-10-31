package br.edu.fagammon.poo.repositorio;

import br.edu.fagammon.poo.dominio.Identificavel;
import br.edu.fagammon.poo.excecao.EntidadeNaoEncontradaException;

import java.util.List;
import java.util.Optional;

/** Cadastro sem substituição silenciosa de entidades com a mesma identidade. */
public interface Repositorio<T extends Identificavel> {
    void adicionar(T entidade);

    Optional<T> buscarPorId(String id);

    /** Retorna uma cópia não modificável, na ordem de cadastro. */
    List<T> listar();

    default T obterPorId(String id) {
        return buscarPorId(id).orElseThrow(() -> new EntidadeNaoEncontradaException(id));
    }
}
