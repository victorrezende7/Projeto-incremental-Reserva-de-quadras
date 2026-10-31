package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.politica.CancelamentoLivre;
import br.edu.fagammon.poo.politica.PoliticaCancelamento;
import br.edu.fagammon.poo.servico.GerenciadorReservas;
import br.edu.fagammon.poo.excecao.CancelamentoNaoPermitidoException;
import br.edu.fagammon.poo.excecao.TransicaoReservaInvalidaException;
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
    private PoliticaCancelamento politicaCancelamento;

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

        politicaCancelamento = new CancelamentoLivre();

        re1 = gerenciadorReservas.criarReserva(
                p1, q1, periodo, Modalidade.BASQUETE,politicaCancelamento
        );


    }


    @Test
    void deveCriarReservaValida(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de Futsal e volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva reserva1 = new Reserva(p1,q1,periodo,Modalidade.FUTSAL, politicaCancelamento);
        assertEquals(StatusReserva.CONFIRMADA, reserva1.getStatusReserva() );
    }

    @Test
    void deveRecusarModalidadeIncompativel(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));

        assertThrows(IllegalArgumentException.class,()->{
            new Reserva(p1,q1,periodo,Modalidade.FUTSAL,politicaCancelamento);
        });
    }

    @Test
    void deveRecusarParticipanteNulo(){
        Quadra q1 = new Quadra("01","Quadra de Futsal e volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        assertThrows(IllegalArgumentException.class, ()->{
            new Reserva(null,q1,periodo,Modalidade.HANDEBOL, politicaCancelamento);
        });
    }

    @Test
    void deveRecusarQuadraNula(){
        Participante participante = new Participante("01", "Victor");
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        assertThrows(IllegalArgumentException.class, ()->{
            new Reserva(participante,null,periodo,Modalidade.HANDEBOL,politicaCancelamento);
        });
    }
    @Test
    void deveRecusarPeriodoNulo(){
        Participante participante = new Participante("01", "Victor");
        Quadra quadra = new Quadra("01","Quadra grama sintetica",EnumSet.of(Modalidade.SOCIETY));
        assertThrows(IllegalArgumentException.class, ()->{
            new Reserva(participante,quadra,null,Modalidade.HANDEBOL,politicaCancelamento);
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
           new Reserva(p1,q1,periodo,Modalidade.VOLEIBOL,politicaCancelamento);
        });
    }

    @Test
    void duasReservasDiferentesNaoDevemSerIguais(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.TENIS));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = new Reserva(p1,q1,periodo,Modalidade.TENIS,politicaCancelamento);
        Reserva re2 = new Reserva(p1,q1,periodo,Modalidade.TENIS,politicaCancelamento);
        assertNotEquals(re1,re2);
    }

    @Test
    void reservaDeveSerIgualAElaMesma(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.BASQUETE));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = new Reserva(p1,q1,periodo,Modalidade.BASQUETE,politicaCancelamento);
        assertEquals(re1,re1);
    }

    @Test
    void deveRetornarDadosCorretosDaReserva(){
        Participante p1 = new Participante("01", "Victor");
        Quadra q1 = new Quadra("01","Quadra de volei", EnumSet.of(Modalidade.BASQUETE));
        PeriodoReserva periodo = new PeriodoReserva(
                LocalDateTime.of(2026, 10, 4, 16, 0),
                LocalDateTime.of(2026, 10, 4, 17, 0));
        Reserva re1 = new Reserva(p1,q1,periodo,Modalidade.BASQUETE,politicaCancelamento);
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
        Reserva re1 = gerenciadorReservas.criarReserva(p1,q1,periodo,Modalidade.BASQUETE,politicaCancelamento);
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

    @Test
    void deveRecusarModalidadeNula() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva(p1, q1, periodo, null, politicaCancelamento));
    }

    @Test
    void deveRecusarPoliticaNulaComMensagemClara() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> new Reserva(p1, q1, periodo, Modalidade.BASQUETE, null));
        assertTrue(erro.getMessage().contains("Política"));
    }

    @Test
    void politicaCompostaDeveSerExecutadaAntesDaTransicao() {
        PoliticaCancelamento restritiva = reserva -> {
            assertEquals(StatusReserva.CONFIRMADA, reserva.getStatusReserva());
            throw new CancelamentoNaoPermitidoException("Cancelamento recusado pela política");
        };
        Reserva reserva = new Reserva(p1, q1, periodo, Modalidade.BASQUETE, restritiva);
        assertThrows(CancelamentoNaoPermitidoException.class, reserva::cancelar);
        assertEquals(StatusReserva.CONFIRMADA, reserva.getStatusReserva());
    }

    @Test
    void deveSinalizarTransicaoInvalidaComExcecaoEspecificaSemConsultarPolitica() {
        PoliticaCancelamento politica = reserva -> fail("Não deveria consultar política de uma reserva concluída");
        Reserva reserva = new Reserva(p1, q1, periodo, Modalidade.BASQUETE, politica);
        reserva.concluir();
        assertThrows(TransicaoReservaInvalidaException.class, reserva::cancelar);
        assertThrows(TransicaoReservaInvalidaException.class, reserva::concluir);
        assertEquals(StatusReserva.CONCLUIDA, reserva.getStatusReserva());
    }
}


