package br.edu.fagammon.poo.dominio;

import java.util.EnumSet;
import java.util.UUID;

public class Reserva {
    private final String idReserva;
    private final Participante participante;
    private final Quadra quadra;
    private final PeriodoReserva periodoReserva;
    private final StatusReserva statusReserva;
    private final Modalidade modalidade;

    public Reserva(Participante participante, Quadra quadra, PeriodoReserva periodoReserva, Modalidade modalidade){

        if (participante == null) {
            throw new IllegalArgumentException("Participante não pode ser nulo");
        }
        if (quadra == null) {
            throw new IllegalArgumentException("Quadra não pode ser nula");
        }
        if (periodoReserva == null) {
            throw new IllegalArgumentException("Período da reserva não pode ser nulo");
        }
        if(!quadra.aceitaModalidade(modalidade)){
            throw new IllegalArgumentException("Modalidade não permitida na quadra reservada");
        }
        if(quadra.isAtiva() == false){
            throw new IllegalArgumentException("Quadra desativada não pode ser reservada");
        }
        this.idReserva = UUID.randomUUID().toString();
        this.participante = participante;
        this. quadra = quadra;
        this.periodoReserva = periodoReserva;
        this.modalidade = modalidade;
        this.statusReserva = StatusReserva.CONFIRMADA;
    }

    public String getId(){
        return idReserva;
    }

    public Participante getParticipante(){
        return participante;
    }


}
