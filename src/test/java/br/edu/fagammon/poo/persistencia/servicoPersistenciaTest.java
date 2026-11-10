
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

    @Test
    void deveSalvarECarregarListaVazia() {
        GerenciadorReservas gerenciador = new GerenciadorReservas();

        ServicoPersistencia servico = new ServicoPersistencia();

        Path arquivo = pastaTemporaria.resolve("vazio.json");

        servico.salvar(gerenciador, arquivo);

        GerenciadorReservas restaurado = servico.carregar(arquivo);

        assertTrue(restaurado.getReservas().isEmpty());
    }

    @Test
    void deveRestaurarMultiplasReservas() {
        GerenciadorReservas gerenciador = new GerenciadorReservas();

        Participante participante = new Participante("01", "Joao");

        Quadra quadra = new Quadra(
                "01", "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        PeriodoReserva periodo1 = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 14, 0),
                LocalDateTime.of(2026, 12, 10, 15, 0)
        );

        PeriodoReserva periodo2 = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 16, 0),
                LocalDateTime.of(2026, 12, 10, 17, 0)
        );

        Reserva r1 = gerenciador.criarReserva(
                participante, quadra, periodo1, Modalidade.FUTSAL,
                new CancelamentoLivre(),
                new PrecoFixoPorHora(new BigDecimal("100.00"))
        );

        Reserva r2 = gerenciador.criarReserva(
                participante, quadra, periodo2, Modalidade.FUTSAL,
                new CancelamentoLivre(),
                new PrecoFixoPorHora(new BigDecimal("100.00"))
        );

        ServicoPersistencia servico = new ServicoPersistencia();

        Path arquivo = pastaTemporaria.resolve("multiplas.json");

        servico.salvar(gerenciador, arquivo);

        GerenciadorReservas restaurado = servico.carregar(arquivo);

        assertEquals(2, restaurado.getReservas().size());

        assertTrue(restaurado.getReservas().stream()
                .anyMatch(r -> r.getId().equals(r1.getId())
                        && r.getPeriodoReserva().equals(periodo1)));

        assertTrue(restaurado.getReservas().stream()
                .anyMatch(r -> r.getId().equals(r2.getId())
                        && r.getPeriodoReserva().equals(periodo2)));
    }

    @Test
    void naoDeveRestaurarPeriodoInvalido() throws IOException {
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

        ServicoPersistencia servico = new ServicoPersistencia();

        Path arquivo = pastaTemporaria.resolve("periodoInvalido.json");

        servico.salvar(gerenciador, arquivo);

        String json = Files.readString(arquivo);

        String inicio = "2026-12-10T14:00";
        String fim = "2026-12-10T15:00";

        // O período deve ter sido gravado no formato esperado.
        assertTrue(json.contains(inicio));
        assertTrue(json.contains(fim));

        // Altera o fim para ser igual ao início.
        String jsonInvalido = json.replace(fim, inicio);

        Files.writeString(arquivo, jsonInvalido);

        assertThrows(RuntimeException.class, () -> {
            servico.carregar(arquivo);
        });
    }



}
