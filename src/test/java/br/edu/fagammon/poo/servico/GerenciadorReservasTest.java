package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GerenciadorReservasTest {

    private GerenciadorReservas gerenciador;
    private Participante p1;
    private Participante p2;
    private Quadra quadra1;
    private Quadra quadra2;

    @BeforeEach
    void configurar() {
        gerenciador = new GerenciadorReservas();

        p1 = new Participante("01", "Joao");
        p2 = new Participante("02", "Maria");

        quadra1 = new Quadra(
                "01",
                "Quadra A",
                EnumSet.of(Modalidade.FUTSAL)
        );

        quadra2 = new Quadra(
                "02",
                "Quadra B",
                EnumSet.of(Modalidade.FUTSAL)
        );
    }

    // Auxiliar para criar periodos sem repetir codigo
    private PeriodoReserva periodo(
            int horaInicio, int minutoInicio,
            int horaFim, int minutoFim) {

        return new PeriodoReserva(
                LocalDateTime.of(
                        2026, 10, 27, horaInicio, minutoInicio),
                LocalDateTime.of(
                        2026, 10, 27, horaFim, minutoFim)
        );
    }


    @Test
    void deveCriarReservaValida() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        assertEquals(1, gerenciador.getReservas().size());
        assertEquals(
                StatusReserva.CONFIRMADA,
                gerenciador.getReservas().get(0).getStatusReserva()
        );
    }


    @Test
    void deveImpedirReservasConflitantes() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);
        PeriodoReserva pe2 = periodo(14, 30, 15, 30);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        assertThrows(IllegalArgumentException.class, () -> {
            gerenciador.criarReserva(
                    p2, quadra1, pe2, Modalidade.FUTSAL
            );
        });

        assertEquals(1, gerenciador.getReservas().size());
    }


    @Test
    void devePermitirHorariosConsecutivos() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);
        PeriodoReserva pe2 = periodo(15, 0, 16, 0);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        gerenciador.criarReserva(
                p2, quadra1, pe2, Modalidade.FUTSAL
        );

        assertEquals(2, gerenciador.getReservas().size());
    }


    @Test
    void devePermitirMesmoHorarioEmQuadrasDiferentes() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        gerenciador.criarReserva(
                p2, quadra2, pe1, Modalidade.FUTSAL
        );

        assertEquals(2, gerenciador.getReservas().size());
    }


    @Test
    void devePermitirReservaAposCancelamento() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        Reserva primeira = gerenciador.getReservas().get(0);
        primeira.cancelar();

        gerenciador.criarReserva(
                p2, quadra1, pe1, Modalidade.FUTSAL
        );

        assertEquals(2, gerenciador.getReservas().size());

        assertEquals(
                StatusReserva.CANCELADA,
                primeira.getStatusReserva()
        );

        assertEquals(
                StatusReserva.CONFIRMADA,
                gerenciador.getReservas().get(1).getStatusReserva()
        );
    }


    @Test
    void deveRetornarReservasCadastradas() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);
        PeriodoReserva pe2 = periodo(16, 0, 17, 0);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        gerenciador.criarReserva(
                p2, quadra1, pe2, Modalidade.FUTSAL
        );

        List<Reserva> lista = gerenciador.getReservas();

        assertEquals(2, lista.size());
        assertEquals(p1, lista.get(0).getParticipante());
        assertEquals(p2, lista.get(1).getParticipante());
    }


    @Test
    void naoDevePermitirModificarListaExternamente() {
        PeriodoReserva pe1 = periodo(14, 0, 15, 0);

        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL
        );

        List<Reserva> lista = gerenciador.getReservas();

        assertThrows(UnsupportedOperationException.class, () -> {
            lista.clear();
        });

        assertEquals(1, gerenciador.getReservas().size());
    }
}

