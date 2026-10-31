package br.edu.fagammon.poo.excecao;

public class EntidadeNaoEncontradaException extends IllegalArgumentException {
    public EntidadeNaoEncontradaException(String id) {
        super("Entidade não encontrada para o identificador: " + id);
    }
}
