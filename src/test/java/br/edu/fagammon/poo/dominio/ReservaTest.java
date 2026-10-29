package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.servico.GerenciadorReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.EnumSet;


public class ReservaTest {
    private GerenciadorReservas gerenciadorReservas;
    private Participante p1;
    private Quadra q1;
    private PeriodoReserva periodo;
    private Reserva re1;

    @BeforeEach
    void prepararTeste() {
        gerenciadorReservas = new GerenciadorReservas();

        p1 = new Participante("01", "Victor");

        q1 = new Quadra(
                "01",
                "Quadra de Basquete",
                EnumSet.of(Modalidade.BASQUETE)
        );

        periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0)
        );

        re1 = gerenciadorReservas.criarReserva(
                p1, q1, periodo, Modalidade.BASQUETE
        );
    }


    @Test
    void deveCriarReservaValida(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de Futsal e volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva reserva1 = new Reserva(p1,q1,periodo,Modalidade.FUTSAL);
        assertEquals(StatusReserva.CONFIRMADA, reserva1.getStatusReserva() );
    }

    @Test
    void reservaAceitaModalidadeInvalida(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));

        assertThrows(IllegalArgumentException.class,()->{
            new Reserva(p1,q1,periodo,Modalidade.FUTSAL);
        });
    }

    @Test
    void deveRecusarParticipanteNulo(){
        Quadra q1 = new Quadra("01","Quadra de Futsal e volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        assertThrows(IllegalArgumentException.class, ()->{
            new Reserva(null,q1,periodo,Modalidade.HANDEBOL);
        });
    }

    @Test
    void deveRecusarQuadraNula(){
        Participante participante = new Participante("01", "Victor");
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        assertThrows(IllegalArgumentException.class, ()->{
            new Reserva(participante,null,periodo,Modalidade.HANDEBOL);
        });
    }
    @Test
    void deveRecusarPeriodoNulo(){
        Participante participante = new Participante("01", "Victor");
        Quadra quadra = new Quadra("01","Quadra grama sintetica",EnumSet.of(Modalidade.SOCIETY));
        assertThrows(IllegalArgumentException.class, ()->{
            new Reserva(participante,quadra,null,Modalidade.HANDEBOL);
        });
    }

    @Test
    void deveRecusarReservaEmQuadraInativa(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        q1.desativar();
        assertThrows(IllegalArgumentException.class,()->{
           new Reserva(p1,q1,periodo,Modalidade.VOLEIBOL);
        });
    }

    @Test
    void duasReservasDiferentesNaoDevemSerIguais(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.TENIS));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = new Reserva(p1,q1,periodo,Modalidade.TENIS);
        Reserva re2 = new Reserva(p1,q1,periodo,Modalidade.TENIS);
        assertNotEquals(re1,re2);
    }

    @Test
    void reservaDeveSerIgualAElaMesma(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.BASQUETE));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = new Reserva(p1,q1,periodo,Modalidade.BASQUETE);
        assertEquals(re1,re1);
    }

    @Test
    void deveRetornarDadosCorretosDaReserva(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.BASQUETE));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = new Reserva(p1,q1,periodo,Modalidade.BASQUETE);
        assertEquals(p1,re1.getParticipante());
        assertEquals(q1,re1.getQuadra());
        assertEquals(periodo,re1.getPeriodoReserva());
        assertEquals(StatusReserva.CONFIRMADA, re1.getStatusReserva());
    }


    @Test
    void deveCancelarReservaConfirmada(){
        GerenciadorReservas gerenciadorReservas = new GerenciadorReservas();
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.BASQUETE));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = gerenciadorReservas.criarReserva(p1,q1,periodo,Modalidade.BASQUETE);
        re1.cancelar();
        assertEquals(StatusReserva.CANCELADA, re1.getStatusReserva());
    }

    @Test
    void deveConcluirReservaConfirmada(){
        re1.concluir();
        assertEquals(StatusReserva.CONCLUIDA, re1.getStatusReserva());
    }

    @Test
    void naoDeveCancelarReservaCancelada(){
        re1.cancelar();
        assertThrows(IllegalStateException.class, ()-> re1.cancelar());
    }

    @Test
    void naoDeveCancelarReservaConcluida(){
        re1.concluir();
        assertThrows(IllegalStateException.class,()-> re1.cancelar());
    }

    @Test
    void naoDeveConcluirReservaCancelada(){
        re1.cancelar();
        assertThrows(IllegalStateException.class,()-> re1.concluir());
    }

    @Test
    void naoDeveConcluirReservaConcluida(){
        re1.concluir();
        assertThrows(IllegalStateException.class,()->re1.concluir());
    }

    @Test
    void deveManterStatusAposTransicaoInvalida(){
            re1.concluir();
            assertThrows(IllegalStateException.class, () -> re1.cancelar());
            assertEquals(StatusReserva.CONCLUIDA, re1.getStatusReserva());
    }
}


