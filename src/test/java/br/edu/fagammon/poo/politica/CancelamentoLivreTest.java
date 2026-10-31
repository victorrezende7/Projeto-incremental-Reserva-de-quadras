package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class CancelamentoLivreTest {
    @Test
    void devePermitirCancelarReservaSemRestricaoDeAntecedencia() {
        PoliticaCancelamento politica = new CancelamentoLivre();
        Reserva reserva = new Reserva(new Participante("p1", "Ana"),
                new Quadra("q1", "Tênis", EnumSet.of(Modalidade.TENIS)),
                new PeriodoReserva(LocalDateTime.of(2026, 1, 1, 10, 0),
                        LocalDateTime.of(2026, 1, 1, 11, 0)), Modalidade.TENIS, politica);
        reserva.cancelar();
        assertEquals(StatusReserva.CANCELADA, reserva.getStatusReserva());
    }

    @Test
    void deveRecusarReservaNula() {
        assertThrows(IllegalArgumentException.class, () -> new CancelamentoLivre().validarCancelamento(null));
    }
}
