package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.Reserva;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CancelamentoComAntecedencia implements PoliticaCancelamento {
    @Override
    public void validarCancelamento(Reserva reserva) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicio = reserva.getPeriodoReserva().inicio();
        Duration tempoRestante = Duration.between(agora, inicio);
        if (tempoRestante.compareTo(Duration.ofHours(24)) < 0) {
            throw new IllegalStateException(
                    "O cancelamento exige pelo menos 24 horas de antecedência"
            );
        }
    }
}
