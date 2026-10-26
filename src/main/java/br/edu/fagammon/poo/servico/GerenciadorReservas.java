package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;

import java.util.ArrayList;
import java.util.List;

public class GerenciadorReservas {
    private final List<Reserva> reservas;

    public GerenciadorReservas() {
        this.reservas = new ArrayList<>();
    }

    public void criarReserva(Participante participante, Quadra quadra, PeriodoReserva periodo, Modalidade modalidade) {
        Reserva novaReserva  = new Reserva(participante,quadra,periodo,modalidade);
        for (Reserva existente : reservas) {
            if(existente.getQuadra().equals(quadra) && existente.getPeriodoReserva().sobrepoe(periodo) && existente.getStatusReserva() == StatusReserva.CONFIRMADA){
                throw new IllegalArgumentException("Já existe uma reserva feita para essa quadra nesse horario");
            }
        }
        reservas.add(novaReserva);
    }

    public List<Reserva> getReservas(){
        return List.copyOf(reservas);
    }
}
