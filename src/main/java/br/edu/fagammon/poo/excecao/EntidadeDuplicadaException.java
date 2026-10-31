package br.edu.fagammon.poo.excecao;

public class EntidadeDuplicadaException extends IllegalArgumentException {
    public EntidadeDuplicadaException(String id) {
        super("Já existe uma entidade cadastrada com o identificador: " + id);
    }
}
