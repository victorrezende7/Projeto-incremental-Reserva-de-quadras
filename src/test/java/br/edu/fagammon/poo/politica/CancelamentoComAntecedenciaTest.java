package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.excecao.CancelamentoNaoPermitidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class CancelamentoComAntecedenciaTest {
    private final Clock relogio = Clock.fixed(
            Instant.parse("2026-10-31T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
    private final LocalDateTime agora = LocalDateTime.now(relogio);

    private Reserva reserva(LocalDateTime inicio, PoliticaCancelamento politica) {
        return new Reserva(new Participante("01", "Victor"),
                new Quadra("01", "Quadra de Futsal", EnumSet.of(Modalidade.FUTSAL)),
                new PeriodoReserva(inicio, inicio.plusHours(1)), Modalidade.FUTSAL, politica);
    }

    @ParameterizedTest
    @ValueSource(longs = {24, 25, 48})
    void devePermitirCancelamentoAPartirDe24Horas(long horas) {
        Reserva reserva = reserva(agora.plusHours(horas), new CancelamentoComAntecedencia(relogio));
        reserva.cancelar();
        assertEquals(StatusReserva.CANCELADA, reserva.getStatusReserva());
    }

    @ParameterizedTest
    @ValueSource(longs = {-24, 0, 1, 23})
    void deveRecusarCancelamentoSemAntecedenciaMinimaEManterEstado(long horas) {
        Reserva reserva = reserva(agora.plusHours(horas), new CancelamentoComAntecedencia(relogio));
        assertThrows(CancelamentoNaoPermitidoException.class, reserva::cancelar);
        assertEquals(StatusReserva.CONFIRMADA, reserva.getStatusReserva());
    }

    @Test
    void deveRecusarUmNanossegundoAbaixoDoLimite() {
        Reserva reserva = reserva(agora.plusHours(24).minusNanos(1), new CancelamentoComAntecedencia(relogio));
        assertThrows(CancelamentoNaoPermitidoException.class, reserva::cancelar);
        assertEquals(StatusReserva.CONFIRMADA, reserva.getStatusReserva());
    }

    @Test
    void deveAceitarUmNanossegundoAcimaDoLimite() {
        Reserva reserva = reserva(agora.plusHours(24).plusNanos(1), new CancelamentoComAntecedencia(relogio));
        assertDoesNotThrow(reserva::cancelar);
    }

    @Test
    void deveRespeitarAntecedenciaConfigurada() {
        PoliticaCancelamento politica = new CancelamentoComAntecedencia(Duration.ofHours(2), relogio);
        assertDoesNotThrow(reserva(agora.plusHours(2), politica)::cancelar);
        assertThrows(CancelamentoNaoPermitidoException.class,
                reserva(agora.plusHours(2).minusNanos(1), politica)::cancelar);
    }

    @Test
    void deveAceitarAntecedenciaZeroSemLiberarCancelamentoNoPassado() {
        PoliticaCancelamento politica = new CancelamentoComAntecedencia(Duration.ZERO, relogio);
        assertDoesNotThrow(reserva(agora, politica)::cancelar);
        assertThrows(CancelamentoNaoPermitidoException.class, reserva(agora.minusNanos(1), politica)::cancelar);
    }

    @Test
    void deveRecusarConfiguracaoInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new CancelamentoComAntecedencia(null));
        assertThrows(IllegalArgumentException.class, () -> new CancelamentoComAntecedencia(null, relogio));
        assertThrows(IllegalArgumentException.class, () -> new CancelamentoComAntecedencia(Duration.ofHours(-1), relogio));
        assertThrows(IllegalArgumentException.class, () -> new CancelamentoComAntecedencia(Duration.ZERO, null));
    }

    @Test
    void deveRecusarReservaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new CancelamentoComAntecedencia(relogio).validarCancelamento(null));
    }

    @Test
    void construtorPadraoDeveContinuarDisponivel() {
        assertDoesNotThrow(() -> new CancelamentoComAntecedencia()
                .validarCancelamento(reserva(LocalDateTime.now().plusDays(3), new CancelamentoLivre())));
    }
}
