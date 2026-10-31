package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.excecao.ConflitoReservaException;
import br.edu.fagammon.poo.excecao.EntidadeDuplicadaException;
import br.edu.fagammon.poo.excecao.TransicaoReservaInvalidaException;
import br.edu.fagammon.poo.politica.CancelamentoComAntecedencia;
import br.edu.fagammon.poo.politica.CancelamentoLivre;
import br.edu.fagammon.poo.repositorio.Repositorio;
import br.edu.fagammon.poo.repositorio.RepositorioEmMemoria;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvolucaoReservasTest {
    @Test
    void deveIntegrarCadastroPoliticasAgendaEHistoricoSemEfeitosParciais() {
        Repositorio<Participante> participantes = new RepositorioEmMemoria<>();
        Repositorio<Quadra> quadras = new RepositorioEmMemoria<>();
        participantes.adicionar(new Participante("p1", "Ana"));
        participantes.adicionar(new Participante("p2", "Bia"));
        quadras.adicionar(new Quadra("q1", "Poliesportiva", EnumSet.of(Modalidade.FUTSAL, Modalidade.VOLEIBOL)));
        assertThrows(EntidadeDuplicadaException.class,
                () -> quadras.adicionar(new Quadra("q1", "Outra", EnumSet.of(Modalidade.TENIS))));

        Participante ana = participantes.obterPorId("p1");
        Participante bia = participantes.obterPorId("p2");
        Quadra quadra = quadras.obterPorId("q1");
        Clock relogio = Clock.fixed(Instant.parse("2026-10-31T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        LocalDateTime inicio = LocalDateTime.now(relogio).plusDays(2);
        PeriodoReserva periodo = new PeriodoReserva(inicio, inicio.plusHours(1));
        GerenciadorReservas gerenciador = new GerenciadorReservas();

        Reserva original = gerenciador.criarReserva(ana, quadra, periodo, Modalidade.FUTSAL,
                new CancelamentoComAntecedencia(relogio));
        assertThrows(ConflitoReservaException.class,
                () -> gerenciador.criarReserva(bia, quadra, periodo, Modalidade.VOLEIBOL, new CancelamentoLivre()));
        assertEquals(List.of(original), gerenciador.getReservas());

        gerenciador.cancelarReserva(original.getId());
        assertTrue(gerenciador.estaDisponivel(quadra, periodo, Modalidade.VOLEIBOL));
        Reserva substituta = gerenciador.criarReserva(bia, quadra, periodo, Modalidade.VOLEIBOL, new CancelamentoLivre());
        gerenciador.concluirReserva(substituta.getId());
        assertThrows(TransicaoReservaInvalidaException.class, () -> gerenciador.cancelarReserva(substituta.getId()));
        assertFalse(gerenciador.estaDisponivel(quadra, periodo, Modalidade.FUTSAL));

        quadra.desativar();
        PeriodoReserva seguinte = new PeriodoReserva(periodo.fim(), periodo.fim().plusHours(1));
        assertThrows(IllegalArgumentException.class,
                () -> gerenciador.criarReserva(ana, quadra, seguinte, Modalidade.FUTSAL, new CancelamentoLivre()));
        quadra.ativar();
        Reserva ultima = gerenciador.criarReserva(ana, quadra, seguinte, Modalidade.FUTSAL, new CancelamentoLivre());
        gerenciador.cancelarReserva(ultima.getId());

        assertEquals(List.of(original, substituta, ultima), gerenciador.reservasOrdenadasPorData());
        assertEquals(List.of(original, ultima), gerenciador.buscaPorStatus(StatusReserva.CANCELADA));
        assertEquals(List.of(substituta), gerenciador.buscaPorParticipante(bia));
        assertEquals(List.of(original, substituta), gerenciador.buscaPorPeriodo(periodo));
        assertSame(quadra, substituta.getQuadra());
        assertTrue(gerenciador.estaDisponivel(quadra, seguinte, Modalidade.FUTSAL));
    }
}
