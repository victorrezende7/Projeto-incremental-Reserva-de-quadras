package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.Reserva;
import br.edu.fagammon.poo.excecao.CancelamentoNaoPermitidoException;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

public class CancelamentoComAntecedencia implements PoliticaCancelamento {
    private final Duration antecedenciaMinima;
    private final Clock relogio;

    public CancelamentoComAntecedencia() {
        this(Duration.ofHours(24), Clock.systemDefaultZone());
    }

    public CancelamentoComAntecedencia(Clock relogio) {
        this(Duration.ofHours(24), relogio);
    }

    public CancelamentoComAntecedencia(Duration antecedenciaMinima, Clock relogio) {
        if (antecedenciaMinima == null || antecedenciaMinima.isNegative()) {
            throw new IllegalArgumentException("Antecedência mínima deve ser uma duração não negativa");
        }
        if (relogio == null) {
            throw new IllegalArgumentException("Relógio não pode ser nulo");
        }
        this.antecedenciaMinima = antecedenciaMinima;
        this.relogio = relogio;
    }

    @Override
    public void validarCancelamento(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }
        LocalDateTime agora = LocalDateTime.now(relogio);
        LocalDateTime inicio = reserva.getPeriodoReserva().inicio();
        Duration tempoRestante = Duration.between(agora, inicio);
        if (tempoRestante.compareTo(antecedenciaMinima) < 0) {
            throw new CancelamentoNaoPermitidoException(
                    "O cancelamento exige antecedência mínima de " + antecedenciaMinima
            );
        }
    }
}
