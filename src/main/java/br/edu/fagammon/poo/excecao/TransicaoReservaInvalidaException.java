package br.edu.fagammon.poo.excecao;

import br.edu.fagammon.poo.dominio.StatusReserva;

public class TransicaoReservaInvalidaException extends IllegalStateException {
    public TransicaoReservaInvalidaException(StatusReserva atual, StatusReserva destino) {
        super("Não é possível alterar a reserva de " + atual + " para " + destino);
    }
}
