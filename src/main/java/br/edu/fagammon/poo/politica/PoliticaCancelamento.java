package br.edu.fagammon.poo.politica;
import br.edu.fagammon.poo.dominio.Reserva;

/** Estratégia que valida o cancelamento sem alterar o estado da reserva. */
@FunctionalInterface
public interface PoliticaCancelamento {
    void validarCancelamento(Reserva reserva);
}
