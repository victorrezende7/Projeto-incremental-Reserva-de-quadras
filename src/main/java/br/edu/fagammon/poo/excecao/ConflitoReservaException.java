package br.edu.fagammon.poo.excecao;

public class ConflitoReservaException extends IllegalArgumentException {
    public ConflitoReservaException(String idReserva) {
        super("Já existe uma reserva para essa quadra nesse horário: " + idReserva);
    }
}
