
package br.edu.fagammon.poo.persistencia;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.CancelamentoLivre;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversorDadosSistemaTest {

    @Test
    void deveConverterERestaurarReserva() {
        Participante participante = new Participante("01", "Joao");

        Quadra quadra = new Quadra(
                "01",
                "Quadra de Futsal",
                EnumSet.of(Modalidade.FUTSAL)
        );

        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 12, 10, 14, 0),
                LocalDateTime.of(2026, 12, 10, 15, 0)
        );

        Reserva original = new Reserva(
                participante,
                quadra,
                periodo,
                Modalidade.FUTSAL,
                new CancelamentoLivre(),
                new BigDecimal("100.00")
        );

        ConversorDadosSistema conversor = new ConversorDadosSistema();

        DadosSistema dados = conversor.paraDados(List.of(original));
        List<Reserva> restauradas = conversor.paraReservas(dados);

        assertEquals(1, restauradas.size());

        Reserva recuperada = restauradas.get(0);

        assertEquals(original.getId(), recuperada.getId());
        assertEquals(original.getStatusReserva(), recuperada.getStatusReserva());
        assertEquals(original.getParticipante(), recuperada.getParticipante());
        assertEquals(original.getQuadra(), recuperada.getQuadra());
        assertEquals(original.getPeriodoReserva(), recuperada.getPeriodoReserva());
        assertEquals(0, original.getPreco().compareTo(recuperada.getPreco()));
    }
}
