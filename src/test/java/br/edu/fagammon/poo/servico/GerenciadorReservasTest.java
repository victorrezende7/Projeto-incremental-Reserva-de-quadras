package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.CancelamentoLivre;
import br.edu.fagammon.poo.politica.PoliticaCancelamento;
import br.edu.fagammon.poo.politica.CancelamentoComAntecedencia;
import br.edu.fagammon.poo.excecao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    @ParameterizedTest
    @CsvSource({"13,30,14,30", "14,30,15,30", "13,0,16,0", "14,15,14,45", "14,0,15,0"})
    void deveRecusarTodaSobreposicaoSemAlterarCadastro(int horaInicio, int minutoInicio,
                                                     int horaFim, int minutoFim) {
        Reserva original = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        PeriodoReserva conflito = new PeriodoReserva(pe1.inicio().toLocalDate().atTime(horaInicio, minutoInicio),
                pe1.inicio().toLocalDate().atTime(horaFim, minutoFim));
        ConflitoReservaException erro = assertThrows(ConflitoReservaException.class,
                () -> gerenciador.criarReserva(p2, quadra1, conflito, Modalidade.FUTSAL, politica));
        assertTrue(erro.getMessage().contains(original.getId()));
        assertEquals(List.of(original), gerenciador.getReservas());
        assertEquals(StatusReserva.CONFIRMADA, original.getStatusReserva());
    }

    @Test
    void devePermitirIntervalosQueSeTocamNasDuasExtremidades() {
        gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        PeriodoReserva anterior = new PeriodoReserva(pe1.inicio().minusHours(1), pe1.inicio());
        PeriodoReserva posterior = new PeriodoReserva(pe1.fim(), pe1.fim().plusHours(1));
        gerenciador.criarReserva(p2, quadra1, anterior, Modalidade.FUTSAL, politica);
        gerenciador.criarReserva(p3, quadra1, posterior, Modalidade.FUTSAL, politica);
        assertEquals(3, gerenciador.getReservas().size());
    }

    @Test
    void conflitoDeveUsarIdentidadeDaQuadra() {
        gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        Quadra outraInstancia = new Quadra(quadra1.getId(), "Mesmo recurso", EnumSet.of(Modalidade.FUTSAL));
        assertThrows(ConflitoReservaException.class,
                () -> gerenciador.criarReserva(p2, outraInstancia, pe1, Modalidade.FUTSAL, politica));
        assertFalse(gerenciador.estaDisponivel(outraInstancia, pe1, Modalidade.FUTSAL));
    }

    @Test
    void reservaConcluidaDevePreservarOcupacaoDoHistorico() {
        Reserva reserva = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        gerenciador.concluirReserva(reserva.getId());
        assertThrows(ConflitoReservaException.class,
                () -> gerenciador.criarReserva(p2, quadra1, pe1, Modalidade.FUTSAL, politica));
        assertFalse(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.FUTSAL));
        assertEquals(List.of(reserva), gerenciador.buscaPorStatus(StatusReserva.CONCLUIDA));
    }

    @Test
    void deveBuscarECancelarReservaPorIdSemRemoverHistorico() {
        Reserva reserva = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        assertSame(reserva, gerenciador.buscarReservaPorId(reserva.getId()));
        gerenciador.cancelarReserva(reserva.getId());
        assertTrue(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.FUTSAL));
        assertEquals(List.of(reserva), gerenciador.buscaPorPeriodo(pe1));
        assertEquals(StatusReserva.CANCELADA, reserva.getStatusReserva());
    }

    @Test
    void falhaNoCancelamentoDevePreservarReservaEOcupacao() {
        Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneOffset.UTC);
        Reserva reserva = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL,
                new CancelamentoComAntecedencia(relogio));
        assertThrows(CancelamentoNaoPermitidoException.class, () -> gerenciador.cancelarReserva(reserva.getId()));
        assertEquals(StatusReserva.CONFIRMADA, reserva.getStatusReserva());
        assertFalse(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.FUTSAL));
        assertEquals(1, gerenciador.getReservas().size());
    }

    @Test
    void deveRecusarOperacoesComIdentificadorAusente() {
        assertThrows(EntidadeNaoEncontradaException.class, () -> gerenciador.buscarReservaPorId("ausente"));
        assertThrows(EntidadeNaoEncontradaException.class, () -> gerenciador.cancelarReserva("ausente"));
        assertThrows(EntidadeNaoEncontradaException.class, () -> gerenciador.concluirReserva("ausente"));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.buscarReservaPorId(null));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.cancelarReserva(" "));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.concluirReserva(""));
        assertTrue(gerenciador.getReservas().isEmpty());
    }

    @Test
    void deveConsultarPorPeriodoUsandoIntersecaoESemIncluirIntervalosAdjacentes() {
        Reserva primeira = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        Reserva segunda = gerenciador.criarReserva(p2, quadra2, pe1, Modalidade.TENIS, politica);
        gerenciador.criarReserva(p3, quadra1, pe2, Modalidade.FUTSAL, politica);
        PeriodoReserva consulta = new PeriodoReserva(pe1.inicio().plusMinutes(30), pe2.inicio());
        assertEquals(List.of(primeira, segunda), gerenciador.buscaPorPeriodo(consulta));
    }

    @Test
    void consultasDevemUsarIdentidadeERetornarVazioSemResultados() {
        Reserva reserva = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        assertEquals(List.of(reserva), gerenciador.buscaPorParticipante(new Participante(p1.getId(), "Outro nome")));
        assertEquals(List.of(reserva), gerenciador.buscaPorQuadra(
                new Quadra(quadra1.getId(), "Outro nome", EnumSet.of(Modalidade.FUTSAL))));
        assertTrue(gerenciador.buscaPorParticipante(p2).isEmpty());
        assertTrue(gerenciador.buscaPorQuadra(quadra2).isEmpty());
        assertTrue(gerenciador.buscaPorStatus(StatusReserva.CANCELADA).isEmpty());
        assertTrue(gerenciador.buscaPorPeriodo(pe2).isEmpty());
    }

    @Test
    void todasAsConsultasDevemRetornarCopiasNaoModificaveis() {
        Reserva original = gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica);
        List<List<Reserva>> consultas = List.of(gerenciador.getReservas(), gerenciador.buscaPorQuadra(quadra1),
                gerenciador.buscaPorParticipante(p1), gerenciador.buscaPorStatus(StatusReserva.CONFIRMADA),
                gerenciador.buscaPorPeriodo(pe1), gerenciador.reservasOrdenadasPorData());
        consultas.forEach(lista -> assertThrows(UnsupportedOperationException.class, lista::clear));
        gerenciador.criarReserva(p2, quadra1, pe2, Modalidade.FUTSAL, politica);
        consultas.forEach(lista -> assertEquals(List.of(original), lista));
        assertEquals(2, gerenciador.getReservas().size());
    }

    @Test
    void ordenacaoDeveSerEstavelSemAlterarOrdemDeCadastro() {
        Reserva tardia = gerenciador.criarReserva(p1, quadra1, pe2, Modalidade.FUTSAL, politica);
        Reserva primeira = gerenciador.criarReserva(p2, quadra1, pe1, Modalidade.FUTSAL, politica);
        Reserva empate = gerenciador.criarReserva(p3, quadra2, pe1, Modalidade.TENIS, politica);
        assertEquals(List.of(primeira, empate, tardia), gerenciador.reservasOrdenadasPorData());
        assertEquals(List.of(tardia, primeira, empate), gerenciador.getReservas());
    }

    @Test
    void deveValidarParametrosObrigatoriosDasConsultas() {
        assertThrows(IllegalArgumentException.class, () -> gerenciador.buscaPorQuadra(null));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.buscaPorParticipante(null));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.buscaPorStatus(null));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.buscaPorPeriodo(null));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.estaDisponivel(null, pe1, Modalidade.FUTSAL));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.estaDisponivel(quadra1, null, Modalidade.FUTSAL));
        assertThrows(IllegalArgumentException.class, () -> gerenciador.estaDisponivel(quadra1, pe1, null));
    }

    @Test
    void disponibilidadeDeveRespeitarAtivacaoEModalidade() {
        assertTrue(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.FUTSAL));
        assertFalse(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.TENIS));
        quadra1.desativar();
        assertFalse(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.FUTSAL));
        assertThrows(IllegalArgumentException.class,
                () -> gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, politica));
        quadra1.ativar();
        assertTrue(gerenciador.estaDisponivel(quadra1, pe1, Modalidade.FUTSAL));
        assertTrue(gerenciador.getReservas().isEmpty());
    }

    @Test
    void criacaoInvalidaNaoDeveDeixarReservaParcial() {
        assertThrows(IllegalArgumentException.class,
                () -> gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.TENIS, politica));
        assertThrows(IllegalArgumentException.class,
                () -> gerenciador.criarReserva(p1, quadra1, pe1, Modalidade.FUTSAL, null));
        assertTrue(gerenciador.getReservas().isEmpty());
    }
}

