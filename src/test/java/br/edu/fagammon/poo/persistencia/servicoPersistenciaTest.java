
package br.edu.fagammon.poo.persistencia;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.*;
import br.edu.fagammon.poo.servico.GerenciadorReservas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicoPersistenciaTest {

    @TempDir
    Path pastaTemporaria;

    @Test
    void deveSalvarECarregarReserva() {

        GerenciadorReservas gerenciador =
                new GerenciadorReservas();

        Participante participante =
                new Participante("01", "Joao");

        Quadra quadra = new Quadra(
                "01",
                "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 14, 0),
                LocalDateTime.of(2026, 12, 10, 15, 0)
        );

        Reserva original = gerenciador.criarReserva(
                participante,
                quadra,
                periodo,
                Modalidade.FUTSAL,
                new CancelamentoLivre(),
                new PrecoFixoPorHora(new BigDecimal("100.00"))
        );

        ServicoPersistencia servico = new ServicoPersistencia();

        Path arquivo = pastaTemporaria.resolve("reservas.json");

        servico.salvar(gerenciador, arquivo);

        GerenciadorReservas restaurado = servico.carregar(arquivo);

        assertEquals(1, restaurado.getReservas().size());

        Reserva recuperada = restaurado.getReservas().get(0);

        assertEquals(original.getId(), recuperada.getId());
        assertEquals(original.getStatusReserva(),
                recuperada.getStatusReserva());
        assertEquals(original.getParticipante(),
                recuperada.getParticipante());
        assertEquals(original.getQuadra(),
                recuperada.getQuadra());
        assertEquals(original.getPeriodoReserva(),
                recuperada.getPeriodoReserva());
        assertEquals(0,
                original.getPreco().compareTo(recuperada.getPreco()));
    }


    @Test
    void naoDeveCarregarJsonInvalido() throws IOException {

        Path arquivo = pastaTemporaria.resolve("reservas.json");

        Files.writeString(arquivo, "{ json invalido }");

        ServicoPersistencia servico = new ServicoPersistencia();

        assertThrows(IllegalStateException.class, () -> {
            servico.carregar(arquivo);
        });
    }

    @Test
    void naoDeveCarregarArquivoInexistente() {

        Path arquivo = pastaTemporaria.resolve("inexistente.json");

        ServicoPersistencia servico = new ServicoPersistencia();

        assertThrows(IllegalStateException.class, () -> {
            servico.carregar(arquivo);
        });
    }


    @Test
    void naoDeveRestaurarReservasConflitantes() {

        Participante participante = new Participante("01", "Joao");

        Quadra quadra = new Quadra(
                "01", "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 14, 0),
                LocalDateTime.of(2026, 12, 10, 15, 0)
        );

        Reserva r1 = new Reserva(
                participante, quadra, periodo, Modalidade.FUTSAL,
                new CancelamentoLivre(), new BigDecimal("100.00")
        );

        Reserva r2 = new Reserva(
                participante, quadra, periodo, Modalidade.FUTSAL,
                new CancelamentoLivre(), new BigDecimal("100.00")
        );

        ConversorDadosSistema conversor = new ConversorDadosSistema();
        DadosSistema dados = conversor.paraDados(List.of(r1, r2));

        Path arquivo = pastaTemporaria.resolve("conflito.json");

        new PersistenciaJson().salvar(dados, arquivo);

        ServicoPersistencia servico = new ServicoPersistencia();

        assertThrows(IllegalArgumentException.class, () -> {
            servico.carregar(arquivo);
        });
    }


    @Test
    void deveRestaurarReservaCancelada() {

        GerenciadorReservas gerenciador = new GerenciadorReservas();

        Participante participante = new Participante("01", "Joao");

        Quadra quadra = new Quadra(
                "01", "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 14, 0),
                LocalDateTime.of(2026, 12, 10, 15, 0)
        );

        Reserva reserva = gerenciador.criarReserva(
                participante, quadra, periodo, Modalidade.FUTSAL,
                new CancelamentoLivre(),
                new PrecoFixoPorHora(new BigDecimal("100.00"))
        );

        gerenciador.cancelarReserva(reserva.getId());

        ServicoPersistencia servico = new ServicoPersistencia();

        Path arquivo = pastaTemporaria.resolve("cancelada.json");

        servico.salvar(gerenciador, arquivo);

        GerenciadorReservas restaurado = servico.carregar(arquivo);

        Reserva recuperada = restaurado.getReservas().get(0);

        assertEquals(StatusReserva.CANCELADA, recuperada.getStatusReserva());
        assertEquals(reserva.getId(), recuperada.getId());
    }


    @Test
    void deveRestaurarQuadraDesativada() {

        GerenciadorReservas gerenciador = new GerenciadorReservas();

        Participante participante = new Participante("01", "Joao");

        Quadra quadra = new Quadra(
                "01", "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 14, 0),
                LocalDateTime.of(2026, 12, 10, 15, 0)
        );

        gerenciador.criarReserva(
                participante, quadra, periodo, Modalidade.FUTSAL,
                new CancelamentoLivre(),
                new PrecoFixoPorHora(new BigDecimal("100.00"))
        );

        quadra.desativar();

        ServicoPersistencia servico = new ServicoPersistencia();

        Path arquivo = pastaTemporaria.resolve("quadra.json");

        servico.salvar(gerenciador, arquivo);

        GerenciadorReservas restaurado = servico.carregar(arquivo);

        Quadra quadraRecuperada =
                restaurado.getReservas().get(0).getQuadra();

        assertFalse(quadraRecuperada.isAtiva());

        PeriodoReserva novoPeriodo = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 11, 14, 0),
                LocalDateTime.of(2026, 12, 11, 15, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            restaurado.criarReserva(
                    participante, quadraRecuperada,
                    novoPeriodo, Modalidade.FUTSAL,
                    new CancelamentoLivre(),
                    new PrecoFixoPorHora(new BigDecimal("100.00"))
            );
        });
    }



}
