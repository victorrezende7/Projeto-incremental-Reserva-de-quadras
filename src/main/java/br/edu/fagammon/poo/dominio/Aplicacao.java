package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.politica.CancelamentoComAntecedencia;
import br.edu.fagammon.poo.politica.CancelamentoLivre;
import br.edu.fagammon.poo.politica.PoliticaCancelamento;
import br.edu.fagammon.poo.excecao.ConflitoReservaException;
import br.edu.fagammon.poo.repositorio.Repositorio;
import br.edu.fagammon.poo.repositorio.RepositorioEmMemoria;
import br.edu.fagammon.poo.servico.GerenciadorReservas;

import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumSet;

public class Aplicacao {
    public static void main(String[] args){
        Repositorio<Participante> participantes = new RepositorioEmMemoria<>();
        Repositorio<Quadra> quadras = new RepositorioEmMemoria<>();
        Quadra quadra01 = new Quadra("01","Quadra de Futsal e Volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        Quadra quadra02 = new Quadra("02","Quadra de Tenis",EnumSet.of(Modalidade.TENIS));

        Participante p1 = new Participante("01", "Victor");
        Participante p2 = new Participante("02", "Marcos");
        Participante p3 = new Participante("03", "Lucas");
        participantes.adicionar(p1);
        participantes.adicionar(p2);
        participantes.adicionar(p3);
        quadras.adicionar(quadra01);
        quadras.adicionar(quadra02);

        Clock relogio = Clock.systemDefaultZone();
        LocalDate dia = LocalDate.now(relogio).plusDays(3);
        PeriodoReserva pe1 = new PeriodoReserva(dia.atTime(14, 0), dia.atTime(15, 0));

        PeriodoReserva pe2 = new PeriodoReserva(dia.atTime(15, 0), dia.atTime(16, 0));

        PeriodoReserva pe3 = new PeriodoReserva(dia.atTime(18, 0), dia.atTime(20, 0));

        PoliticaCancelamento cancelamentoLivre = new CancelamentoLivre();
        PoliticaCancelamento cancelamentoAntecedencia = new CancelamentoComAntecedencia(relogio);

        GerenciadorReservas gerenciador = new GerenciadorReservas();
        Reserva r1 = gerenciador.criarReserva(p1, quadra01,pe1,Modalidade.FUTSAL,cancelamentoLivre);
        Reserva r2 = gerenciador.criarReserva(p2, quadra01, pe2, Modalidade.FUTSAL,cancelamentoLivre);
        Reserva r3 = gerenciador.criarReserva(p3, quadra01, pe3, Modalidade.FUTSAL,cancelamentoAntecedencia);
        try {
            gerenciador.criarReserva(p2, quadra01, pe1, Modalidade.FUTSAL, cancelamentoLivre);
        } catch (ConflitoReservaException excecao) {
            System.out.println("Reserva recusada: " + excecao.getMessage());
        }
        gerenciador.concluirReserva(r1.getId());
        gerenciador.cancelarReserva(r2.getId());
        gerenciador.cancelarReserva(r3.getId());
        System.out.println("Participantes cadastrados: " + participantes.listar().size());
        System.out.println("Quadras cadastradas: " + quadras.listar().size());
        System.out.println("Horário liberado após cancelamento: "
                + gerenciador.estaDisponivel(quadra01, pe2, Modalidade.FUTSAL));
        gerenciador.reservasOrdenadasPorData().forEach(System.out::println);
    }
}
