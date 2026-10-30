package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.PoliticaCancelamento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GerenciadorReservas {
    private final List<Reserva> reservas;
    public GerenciadorReservas() {
        this.reservas = new ArrayList<>();
    }

    public Reserva criarReserva(Participante participante, Quadra quadra, PeriodoReserva periodo, Modalidade modalidade, PoliticaCancelamento politicaCancelamento) {
        Reserva novaReserva  = new Reserva(participante,quadra,periodo,modalidade, politicaCancelamento);
        for (Reserva existente : reservas) {
            if(existente.getQuadra().equals(quadra) && existente.getPeriodoReserva().sobrepoe(periodo) && existente.getStatusReserva() == StatusReserva.CONFIRMADA){
                throw new IllegalArgumentException("Já existe uma reserva feita para essa quadra nesse horario");
            }
        }
        reservas.add(novaReserva);
        return novaReserva;
    }

    public List<Reserva> getReservas(){
        return List.copyOf(reservas);
    }

    public List<Reserva> buscaPorQuadra(Quadra quadra){
        return reservas.stream()
                .filter(reserva -> reserva.getQuadra().equals(quadra))
                .toList();
    }

    public List<Reserva> buscaPorParticipante(Participante participante){
        return reservas.stream()
                .filter(reserva -> reserva.getParticipante().equals(participante))
                .toList();

    }

    public List<Reserva> buscaPorStatus(StatusReserva status){
        return reservas.stream()
                .filter(reserva -> reserva.getStatusReserva() == status)
                .toList();
    }

    public List<Reserva> reservasOrdenadasPorData(){
        return reservas.stream()
                .sorted(Comparator.comparing(reserva -> reserva.getPeriodoReserva().inicio()))
                .toList();
    }


}
