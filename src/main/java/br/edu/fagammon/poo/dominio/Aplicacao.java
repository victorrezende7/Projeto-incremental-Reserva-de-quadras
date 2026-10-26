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

        PeriodoReserva pe1 = new PeriodoReserva(LocalDateTime.of(
                2026,10,26,14,0,0),
                LocalDateTime.of(2026,10,26,15,0,0));

        PeriodoReserva pe2 = new PeriodoReserva(LocalDateTime.of(
                2026,10,26,14,30,0),
                LocalDateTime.of(2026,10,26,15,30,0));

        GerenciadorReservas gerenciador = new GerenciadorReservas();
        gerenciador.criarReserva(p1, quadra01,pe1,Modalidade.FUTSAL);
        gerenciador.criarReserva(p2, quadra02, pe1, Modalidade.TENIS);

        for (Reserva reserva : gerenciador.getReservas()) {
            System.out.println(reserva);
        }
    }
}
