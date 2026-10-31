package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.politica.PoliticaCancelamento;
import br.edu.fagammon.poo.excecao.TransicaoReservaInvalidaException;

import java.util.Objects;
import java.util.UUID;

public class Reserva implements Identificavel {
    private final String idReserva;
    private final Participante participante;
    private final Quadra quadra;
    private final PeriodoReserva periodoReserva;
    private StatusReserva statusReserva;
    private final Modalidade modalidade;
    private final PoliticaCancelamento politicaCancelamento;

    public Reserva(Participante participante, Quadra quadra, PeriodoReserva periodoReserva, Modalidade modalidade, PoliticaCancelamento politicaCancelamento){

        if (participante == null) {
            throw new IllegalArgumentException("Participante não pode ser nulo");
        }
        if (quadra == null) {
            throw new IllegalArgumentException("Quadra não pode ser nula");
        }
        if (periodoReserva == null) {
            throw new IllegalArgumentException("Período da reserva não pode ser nulo");
        }
        if (modalidade == null) {
            throw new IllegalArgumentException("Modalidade não pode ser nula");
        }
        if (politicaCancelamento == null) {
            throw new IllegalArgumentException("Política de cancelamento não pode ser nula");
        }
        if(!quadra.aceitaModalidade(modalidade)){
            throw new IllegalArgumentException("Modalidade não permitida na quadra reservada");
        }
        if(!quadra.isAtiva()){
            throw new IllegalArgumentException("Quadra desativada não pode ser reservada");
        }
        this.idReserva = UUID.randomUUID().toString();
        this.participante = participante;
        this.quadra = quadra;
        this.periodoReserva = periodoReserva;
        this.modalidade = modalidade;
        this.statusReserva = StatusReserva.CONFIRMADA;
        this.politicaCancelamento = politicaCancelamento;
    }

    @Override
    public String getId(){
        return idReserva;
    }

    public Participante getParticipante(){
        return participante;
    }

    public Quadra getQuadra(){return quadra;}

    public PeriodoReserva getPeriodoReserva(){return periodoReserva;}

    public Modalidade getModalidade(){return modalidade;}

    public StatusReserva getStatusReserva(){return statusReserva;}

    public void cancelar(){
        if(getStatusReserva() != StatusReserva.CONFIRMADA){
            throw new TransicaoReservaInvalidaException(statusReserva, StatusReserva.CANCELADA);
        }
        politicaCancelamento.validarCancelamento(this);
        statusReserva = StatusReserva.CANCELADA;
    }

    public void concluir(){
        if(getStatusReserva() != StatusReserva.CONFIRMADA){
            throw new TransicaoReservaInvalidaException(statusReserva, StatusReserva.CONCLUIDA);
        }
        statusReserva = StatusReserva.CONCLUIDA;
    }



    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(idReserva, reserva.idReserva);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(idReserva);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id='" + idReserva + '\'' +
                ", participante=" + participante.getNome() +
                ", quadra=" + quadra.getNome() +
                ", inicio=" + periodoReserva.inicio() +
                ", fim=" + periodoReserva.fim() +
                ", modalidade=" + modalidade +
                ", status=" + statusReserva +
                '}';
    }
}
