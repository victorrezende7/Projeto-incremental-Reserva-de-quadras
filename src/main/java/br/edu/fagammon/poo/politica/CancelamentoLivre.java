package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.Reserva;

public class CancelamentoLivre implements PoliticaCancelamento{
    @Override
    public void validarCancelamento(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }
    }
}
