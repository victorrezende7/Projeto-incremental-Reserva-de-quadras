package br.edu.fagammon.poo.dominio;

import br.edu.fagammon.poo.politica.PoliticaCancelamento;
import br.edu.fagammon.poo.excecao.TransicaoReservaInvalidaException;

import java.math.BigDecimal;
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
    private final BigDecimal preco;


    public Reserva(Participante participante, Quadra quadra, PeriodoReserva periodoReserva, Modalidade modalidade, PoliticaCancelamento politicaCancelamento, BigDecimal preco) {
        this(
                UUID.randomUUID().toString(),
                participante,
                quadra,
                periodoReserva,
                modalidade,
                politicaCancelamento,
                preco,
                StatusReserva.CONFIRMADA,
                false
        );
    }


    private Reserva(String id, Participante participante, Quadra quadra, PeriodoReserva periodoReserva, Modalidade modalidade, PoliticaCancelamento politicaCancelamento, BigDecimal preco, StatusReserva status, boolean restaurando
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido");
        }

        if (participante == null) {
            throw new IllegalArgumentException("Participante nulo");
        }

        if (quadra == null) {
            throw new IllegalArgumentException("Quadra nula");
        }

        if (periodoReserva == null) {
            throw new IllegalArgumentException("Período nulo");
        }

        if (modalidade == null) {
            throw new IllegalArgumentException("Modalidade nula");
        }

        if (politicaCancelamento == null) {
            throw new IllegalArgumentException("Política nula");
        }

        if (!quadra.aceitaModalidade(modalidade)) {
            throw new IllegalArgumentException(
                    "Modalidade não permitida na quadra"
            );
        }

        if (!restaurando && !quadra.isAtiva()) {
            throw new IllegalArgumentException(
                    "Quadra desativada não pode ser reservada"
            );
        }

        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço inválido");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status inválido");
        }

        this.idReserva = id;
        this.participante = participante;
        this.quadra = quadra;
        this.periodoReserva = periodoReserva;
        this.modalidade = modalidade;
        this.politicaCancelamento = politicaCancelamento;
        this.preco = preco;
        this.statusReserva = status;
    }

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

    public BigDecimal getPreco() {return preco;}

    public PoliticaCancelamento getPoliticaCancelamento() {
        return politicaCancelamento;
    }



    public static Reserva restaurar(
            String id,
            Participante participante,
            Quadra quadra,
            PeriodoReserva periodo,
            Modalidade modalidade,
            PoliticaCancelamento politica,
            BigDecimal preco,
            StatusReserva status
    ){
        return new Reserva(
                id,
                participante,
                quadra,
                periodo,
                modalidade,
                politica,
                preco,
                status,
                true
        );
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
