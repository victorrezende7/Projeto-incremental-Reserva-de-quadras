package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.servico.GerenciadorReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class CancelamentoComAntecedenciaTest {
    private Participante p1;
    private Quadra q1;
    private Quadra q2;
    private PeriodoReserva pe1;
    private LocalDateTime inicioBase;
    private PoliticaCancelamento politica;
    private GerenciadorReservas gerenciador;

    @BeforeEach
    void preparaTeste(){
        p1 = new Participante("01","Victor");
        q1 = new Quadra("01","Quadra de Futsal", EnumSet.of(Modalidade.FUTSAL));
        q2 = new Quadra("02","Quadra de Tenis",EnumSet.of(Modalidade.TENIS));
        inicioBase = LocalDateTime.now();
        politica = new CancelamentoComAntecedencia();
        gerenciador = new GerenciadorReservas();
    }

    @Test
    void devePermitirCancelamentocomMaisde24horas(){
        pe1 = new PeriodoReserva(inicioBase.plusHours(48),inicioBase.plusHours(50));
        Reserva re1 = gerenciador.criarReserva(p1,q1,pe1,Modalidade.FUTSAL,politica);
        assertDoesNotThrow(()->re1.cancelar());
    }

    @Test
    void deveRecusarCancelamentoComMenosDe24Horas(){
        pe1 = new PeriodoReserva(inicioBase,inicioBase.plusHours(10));
        Reserva re1 = gerenciador.criarReserva(p1,q1,pe1,Modalidade.FUTSAL,politica);
        assertThrows(IllegalStateException.class,()->re1.cancelar());
    }
}
