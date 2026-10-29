package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.servico.GerenciadorReservas;

import java.time.LocalDateTime;
import java.util.EnumSet;

public class Aplicacao {
    public static void main(String[] args){
        Quadra quadra01 = new Quadra("01","Quadra de Futsal e Volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        Quadra quadra02 = new Quadra("02","Quadra de Tenis",EnumSet.of(Modalidade.TENIS));

        Participante p1 = new Participante("01", "Victor");
        Participante p2 = new Participante("02", "Marcos");
        Participante p3 = new Participante("03", "Lucas");

        PeriodoReserva pe1 = new PeriodoReserva(LocalDateTime.of(
                2026,10,26,14,0,0),
                LocalDateTime.of(2026,10,26,15,0,0));

        PeriodoReserva pe2 = new PeriodoReserva(LocalDateTime.of(
                2026,10,26,16,30,0),
                LocalDateTime.of(2026,10,26,17,30,0));

        PeriodoReserva pe3 = new PeriodoReserva(LocalDateTime.of(2026,10,26,10,0,0),
                LocalDateTime.of(2026,10,26,11,0,0));

        GerenciadorReservas gerenciador = new GerenciadorReservas();
        Reserva r1 = gerenciador.criarReserva(p1, quadra01,pe1,Modalidade.FUTSAL);
        Reserva r2 = gerenciador.criarReserva(p2, quadra01, pe2, Modalidade.FUTSAL);
        Reserva r3 = gerenciador.criarReserva(p3, quadra01, pe3, Modalidade.FUTSAL);


//        for (Reserva reserva : gerenciador.getReservas()) {
//            System.out.println(reserva);
//        }
//        System.out.println(gerenciador.buscaPorQuadra(quadra01));


//        System.out.println(gerenciador.buscaPorParticipante(p1));
//        r1.cancelar();
//        System.out.println(gerenciador.buscaPorStatus(StatusReserva.CANCELADA));
        gerenciador.reservasOrdenadasPorData().forEach(System.out::println);
    }
}
