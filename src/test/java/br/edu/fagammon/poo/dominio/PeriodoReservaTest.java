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

    private PeriodoReserva periodo(int horaInicio, int minInicio, int horaFim, int minFim) {
        return new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, horaInicio, minInicio),
                LocalDateTime.of(2026, 10, 4, horaFim, minFim));
    }

    @Test
    void deveDetectarSobreposicaoParcial() {
        PeriodoReserva a = periodo(16, 0, 17, 0);
        PeriodoReserva b = periodo(16, 30, 17, 30);
        assertTrue(a.sobrepoe(b));
        assertTrue(b.sobrepoe(a)); // simetria
    }

    @Test
    void deveDetectarSobreposicaoQuandoUmPeriodoContemOutro() {
        PeriodoReserva grande = periodo(15, 0, 18, 0);
        PeriodoReserva dentro = periodo(16, 0, 17, 0);
        assertTrue(grande.sobrepoe(dentro));
        assertTrue(dentro.sobrepoe(grande));
    }

    @Test
    void deveDetectarSobreposicaoDePeriodosIdenticos() {
        assertTrue(periodo(16, 0, 17, 0).sobrepoe(periodo(16, 0, 17, 0)));
    }

    @Test
    void naoDeveSobreporPeriodosConsecutivos() {
        PeriodoReserva a = periodo(16, 0, 17, 0);
        PeriodoReserva b = periodo(17, 0, 18, 0);
        assertFalse(a.sobrepoe(b));
        assertFalse(b.sobrepoe(a));
    }

    @Test
    void naoDeveSobreporPeriodosSeparados() {
        assertFalse(periodo(8, 0, 9, 0).sobrepoe(periodo(14, 0, 15, 0)));
    }

    @Test
    void deveRecusarComparacaoComPeriodoNulo() {
        assertThrows(IllegalArgumentException.class, () -> periodo(16, 0, 17, 0).sobrepoe(null));
    }
}
