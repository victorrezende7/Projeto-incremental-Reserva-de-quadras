package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.politica.*;
import br.edu.fagammon.poo.excecao.ConflitoReservaException;
import br.edu.fagammon.poo.repositorio.Repositorio;
import br.edu.fagammon.poo.repositorio.RepositorioEmMemoria;
import br.edu.fagammon.poo.servico.GerenciadorReservas;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class Aplicacao {
    public static void main(String[] args){
        Repositorio<Participante> participantes = new RepositorioEmMemoria<>();
        Repositorio<Quadra> quadras = new RepositorioEmMemoria<>();
        Quadra quadra01 = new Quadra("01","Quadra de Futsal e Volei", EnumSet.of(Modalidade.FUTSAL,Modalidade.VOLEIBOL));
        Quadra quadra02 = new Quadra("02","Quadra de Tenis",EnumSet.of(Modalidade.TENIS));
        Quadra quadra03 = new Quadra("03","Campo Society",EnumSet.of(Modalidade.SOCIETY));

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

        PeriodoReserva pe3 = new PeriodoReserva(dia.atTime(18, 30), dia.atTime(20, 0));

        PoliticaCancelamento cancelamentoLivre = new CancelamentoLivre();
        PoliticaCancelamento cancelamentoAntecedencia = new CancelamentoComAntecedencia(relogio);


        PoliticaPreco precoFixo = new PrecoFixoPorHora(new BigDecimal("50"));

        Map<Modalidade, BigDecimal> precosEspeciais =
                new EnumMap<>(Modalidade.class);

        precosEspeciais.put(Modalidade.TENIS, new BigDecimal("80.00"));
        precosEspeciais.put(Modalidade.SOCIETY, new BigDecimal("100.00"));

        PoliticaPreco precoPorModalidade = new PrecoPorModalidade(
                new BigDecimal("50.00"),
                precosEspeciais
        );

        GerenciadorReservas gerenciador = new GerenciadorReservas();
        Reserva r1 = gerenciador.criarReserva(p1, quadra01,pe1,Modalidade.FUTSAL,cancelamentoLivre,precoFixo);
        Reserva r2 = gerenciador.criarReserva(p2, quadra02, pe2, Modalidade.TENIS,cancelamentoLivre,precoPorModalidade);
        Reserva r3 = gerenciador.criarReserva(p3, quadra03, pe3, Modalidade.SOCIETY,cancelamentoAntecedencia,precoPorModalidade);

//        try {
//            gerenciador.criarReserva(p2, quadra01, pe1, Modalidade.FUTSAL, cancelamentoLivre,precoPadrao);
//        } catch (ConflitoReservaException excecao) {
//            System.out.println("Reserva recusada: " + excecao.getMessage());
//        }
//        gerenciador.concluirReserva(r1.getId());
//        gerenciador.cancelarReserva(r2.getId());
//        gerenciador.cancelarReserva(r3.getId());
//        System.out.println("Participantes cadastrados: " + participantes.listar().size());
//        System.out.println("Quadras cadastradas: " + quadras.listar().size());
//        System.out.println("Horário liberado após cancelamento: "
//                + gerenciador.estaDisponivel(quadra01, pe2, Modalidade.FUTSAL));
//        gerenciador.reservasOrdenadasPorData().forEach(System.out::println);

        BigDecimal valor = r3.getPreco();
        System.out.println(valor);
    }
}
