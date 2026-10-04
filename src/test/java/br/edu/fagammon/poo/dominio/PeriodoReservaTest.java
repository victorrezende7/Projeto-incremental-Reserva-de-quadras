package br.edu.fagammon.poo.dominio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PeriodoReservaTest {
    @Test
    void validaPeriodoValido(){
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 4, 16, 0);
        LocalDateTime fim = LocalDateTime.of(2026,10,4,17,0);
        PeriodoReserva periodo = new PeriodoReserva(inicio,fim);
        assertEquals(inicio, periodo.inicio());
        assertEquals(fim, periodo.fim());
    }

    @Test
    void deveRecusarPeriodoAntesDoFim(){
        assertThrows(IllegalArgumentException.class,()->{
            new PeriodoReserva(
                    LocalDateTime.of(2026, 10, 4, 16, 0),
                    LocalDateTime.of(2026, 10, 4, 15, 0));
        });
    }

    @Test
    void deveRecusarInicioFimNulo(){
        assertThrows(IllegalArgumentException.class,()->{
            new PeriodoReserva(null,null);
        });
    }

    @Test
    void deveRecusarPeriodoComInicioIgualAoFim(){
        assertThrows(IllegalArgumentException.class,()->{
            new PeriodoReserva(
                    LocalDateTime.of(2026, 10, 4, 16, 0),
                    LocalDateTime.of(2026, 10, 4, 16, 0));
        });
    }

    @Test
    void periodosDiferentesNaoSaoIguais(){
        PeriodoReserva p1 = new PeriodoReserva(LocalDateTime.of(2026,10,5,10,0),
                LocalDateTime.of(2026,10,05,13,0));
        PeriodoReserva p2 = new PeriodoReserva(LocalDateTime.of(2026,10,7,15,0),
                LocalDateTime.of(2026,10,7,18,0));

        assertNotEquals(p1,p2);
    }

    @Test
    void periodosComMesmosValoresDevemSerIguais(){
        PeriodoReserva p1 = new PeriodoReserva(LocalDateTime.of(2026,10,5,10,0),
                LocalDateTime.of(2026,10,5,13,0));
        PeriodoReserva p2 = new PeriodoReserva(LocalDateTime.of(2026,10,5,10,0),
                LocalDateTime.of(2026,10,5,13,0));
        assertEquals(p1,p2);
    }
}
