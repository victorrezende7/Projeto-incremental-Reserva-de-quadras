package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.servico.GerenciadorReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class PrecoFixoPorHoraTest {

    private Participante participante;
    private Quadra quadra;
    private PeriodoReserva periodo;
    private PrecoFixoPorHora politica;
    private CancelamentoLivre cancelamentoLivre;
    private GerenciadorReservas gerenciador;

    @BeforeEach
    void preparaTeste() {
        participante = new Participante("01", "Victor");

        quadra = new Quadra(
                "01",
                "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        periodo = new PeriodoReserva(LocalDateTime.of(2026, 11, 10,18,0,0),
                LocalDateTime.of(2026, 11, 10,20,0,0));

        cancelamentoLivre = new CancelamentoLivre();

        politica = new PrecoFixoPorHora(
                new BigDecimal("60.00")
        );

        gerenciador = new GerenciadorReservas();
    }

    private Reserva criarReserva() {
        PoliticaPreco precoFixo = new PrecoFixoPorHora(new BigDecimal("50"));
        return gerenciador.criarReserva(participante, quadra, periodo, Modalidade.FUTSAL,cancelamentoLivre,precoFixo);
    }

    @Test
    void deveCalcularPrecoDeUmaHora() {
        periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 11, 10, 18, 0),
                LocalDateTime.of(2026, 11, 10, 19, 0)
        );

        BigDecimal preco = politica.calcularPreco(
                periodo, Modalidade.FUTSAL
        );

        assertEquals(0, new BigDecimal("60.00").compareTo(preco));
    }

    @Test
    void deveCalcularPrecoDeDuasHoras() {
        Reserva reserva = criarReserva();

        BigDecimal preco = politica.calcularPreco(periodo,Modalidade.FUTSAL);

        assertEquals(0, new BigDecimal("120.00").compareTo(preco));
    }

    @Test
    void deveCalcularPrecoDeUmaHoraETrintaMinutos() {
        periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 11, 10, 18, 0),
                LocalDateTime.of(2026, 11, 10, 19, 30)
        );

        BigDecimal preco = politica.calcularPreco(
                periodo, Modalidade.FUTSAL
        );

        assertEquals(0, new BigDecimal("90.00").compareTo(preco));
    }

    @Test
    void deveCalcularPrecoDeQuinzeMinutos() {
        periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 11, 10, 18, 0),
                LocalDateTime.of(2026, 11, 10, 18, 15)
        );

        BigDecimal preco = politica.calcularPreco(
                periodo, Modalidade.FUTSAL
        );

        assertEquals(0, new BigDecimal("15.00").compareTo(preco));
    }

    @Test
    void naoDeveAceitarValorPorHoraNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PrecoFixoPorHora(null)
        );
    }

    @Test
    void naoDeveAceitarValorPorHoraNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PrecoFixoPorHora(new BigDecimal("-50.00"))
        );
    }

    @Test
    void naoDeveAceitarValorPorHoraZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PrecoFixoPorHora(BigDecimal.ZERO)
        );
    }


    @Test
    void deveArredondarPrecoParaDuasCasasDecimais() {
        PrecoFixoPorHora politicaEspecial = new PrecoFixoPorHora(
                new BigDecimal("100.00")
        );

        periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 11, 10, 18, 0),
                LocalDateTime.of(2026, 11, 10, 18, 1)
        );

        BigDecimal preco = politicaEspecial.calcularPreco(
                periodo, Modalidade.FUTSAL
        );

        assertEquals(0, new BigDecimal("1.67").compareTo(preco));
    }
}
