package br.edu.fagammon.poo.politica;
import br.edu.fagammon.poo.dominio.Reserva;

public interface PoliticaCancelamento {
    void validarCancelamento(Reserva reserva);
}
