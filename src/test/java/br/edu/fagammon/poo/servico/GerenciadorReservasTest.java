package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.CancelamentoLivre;
import br.edu.fagammon.poo.politica.PoliticaCancelamento;
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
    private Participante p3;
    private PeriodoReserva pe1;
    private PeriodoReserva pe2;
    private PeriodoReserva pe3;
    private Quadra quadra1;
    private Quadra quadra2;
    private Quadra quadra3;
    private PoliticaCancelamento politica;

    @BeforeEach
    void configurar() {
        gerenciador = new GerenciadorReservas();

        p1 = new Participante("01", "Joao");
        p2 = new Participante("02", "Maria");
        p3 = new Participante("03", "Vinicius");

        pe1 = new PeriodoReserva(LocalDateTime.of(2026, 10, 26, 14, 0, 0),
                LocalDateTime.of(2026, 10, 26, 15, 0, 0));
        pe2 = new PeriodoReserva(LocalDateTime.of(
                2026, 10, 26, 16, 30, 0),
                LocalDateTime.of(2026, 10, 26, 17, 30, 0));
        pe3 = new PeriodoReserva(LocalDateTime.of(2026,10,26,18,0,0),
                LocalDateTime.of(2026,10,26,19,0,0));
        quadra1 = new Quadra(
                "01",
                "Quadra A",
                EnumSet.of(Modalidade.FUTSAL)
        );

        quadra2 = new Quadra(
                "02",
                "Quadra B",
                EnumSet.of(Modalidade.TENIS)
        );

        quadra3 = new Quadra(
                "03",
                "Quadra C",
                EnumSet.of(Modalidade.VOLEIBOL)
        );

        politica = new CancelamentoLivre();
    }


    @Test
    void deveCriarReservaValida() {
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        assertEquals(1, gerenciador.getReservas().size());
        assertEquals(
                StatusReserva.CONFIRMADA,
                gerenciador.getReservas().get(0).getStatusReserva()
        );
    }


    @Test
    void deveImpedirReservasConflitantes() {
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        assertThrows(IllegalArgumentException.class, () -> {
            gerenciador.criarReserva(
                    p2, quadra1, pe1, Modalidade.FUTSAL,politica
            );
        });

        assertEquals(1, gerenciador.getReservas().size());
    }


    @Test
    void devePermitirHorariosConsecutivos() {
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        gerenciador.criarReserva(
                p2, quadra1, pe2, Modalidade.FUTSAL,politica
        );

        assertEquals(2, gerenciador.getReservas().size());
    }


    @Test
    void devePermitirMesmoHorarioEmQuadrasDiferentes() {
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        gerenciador.criarReserva(
                p2, quadra2, pe1, Modalidade.TENIS,politica
        );

        assertEquals(2, gerenciador.getReservas().size());
    }


    @Test
    void devePermitirReservaAposCancelamento() {
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        Reserva primeira = gerenciador.getReservas().get(0);
        primeira.cancelar();

        gerenciador.criarReserva(
                p2, quadra1, pe1, Modalidade.FUTSAL,politica
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
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        gerenciador.criarReserva(
                p2, quadra1, pe2, Modalidade.FUTSAL,politica
        );

        List<Reserva> lista = gerenciador.getReservas();

        assertEquals(2, lista.size());
        assertEquals(p1, lista.get(0).getParticipante());
        assertEquals(p2, lista.get(1).getParticipante());
    }


    @Test
    void naoDevePermitirModificarListaExternamente() {
        gerenciador.criarReserva(
                p1, quadra1, pe1, Modalidade.FUTSAL,politica
        );

        List<Reserva> lista = gerenciador.getReservas();

        assertThrows(UnsupportedOperationException.class, () -> {
            lista.clear();
        });

        assertEquals(1, gerenciador.getReservas().size());
    }

    @Test
    void deveBuscarReservasPorQuadra() {
        Reserva re1 = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL,politica);
        Reserva re2 = gerenciador.criarReserva(p2, quadra1, pe2, Modalidade.FUTSAL,politica);
        Reserva re3 = gerenciador.criarReserva(p3, quadra2, pe2, Modalidade.TENIS,politica);

        List<Reserva> resultado = gerenciador.buscaPorQuadra(quadra1);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(re1));
        assertTrue(resultado.contains(re2));
        assertFalse(resultado.contains(re3));
    }

    @Test
    void deveBuscarReservasPorParticipante() {
        Reserva re1 = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL,politica);
        Reserva re2 = gerenciador.criarReserva(p1, quadra1, pe2, Modalidade.FUTSAL,politica);
        Reserva re3 = gerenciador.criarReserva(p2, quadra2, pe2, Modalidade.TENIS,politica);
        List<Reserva> resultado = gerenciador.buscaPorParticipante(p1);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(re1));
        assertTrue(resultado.contains(re2));
        assertFalse(resultado.contains(re3));
    }

    @Test
    void deveBuscarReservaPoRStatus() {
        Reserva re1 = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL,politica);
        Reserva re2 = gerenciador.criarReserva(p1, quadra1, pe2, Modalidade.FUTSAL,politica);
        Reserva re3 = gerenciador.criarReserva(p2, quadra2, pe2, Modalidade.TENIS,politica);
        re1.cancelar();
        re3.cancelar();
        List<Reserva> resultado = gerenciador.buscaPorStatus(StatusReserva.CANCELADA);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(re1));
        assertTrue(resultado.contains(re3));
        assertFalse(resultado.contains(re2));
    }

    @Test
    void retornarListaVazia() {
        List<Reserva> resultado = gerenciador.getReservas();
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveOrdernarPorData(){
        Reserva re1 = gerenciador.criarReserva(p1, quadra1, pe3, Modalidade.FUTSAL,politica);
        Reserva re2 = gerenciador.criarReserva(p1, quadra1, pe2, Modalidade.FUTSAL,politica);
        Reserva re3 = gerenciador.criarReserva(p2, quadra2, pe1, Modalidade.TENIS,politica);
        List<Reserva> resultado = gerenciador.reservasOrdenadasPorData();
        assertEquals(3, resultado.size());
        assertEquals(re2, resultado.get(1));
        assertEquals(re3, resultado.get(0));
        assertEquals(re1, resultado.get(2));
    }

    @Test
    void devePermitirCancelamentoLivre() {
        Reserva re1 = gerenciador.criarReserva(p1, quadra1, pe3, Modalidade.FUTSAL,politica);
        PoliticaCancelamento politica = new CancelamentoLivre();
        assertDoesNotThrow(() -> politica.validarCancelamento(re1));
    }
}

