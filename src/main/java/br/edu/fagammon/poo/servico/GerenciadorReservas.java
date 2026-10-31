package br.edu.fagammon.poo.servico;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.excecao.ConflitoReservaException;
import br.edu.fagammon.poo.politica.PoliticaCancelamento;
import br.edu.fagammon.poo.repositorio.Repositorio;
import br.edu.fagammon.poo.repositorio.RepositorioEmMemoria;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GerenciadorReservas {
    private final Repositorio<Reserva> reservas;
    public GerenciadorReservas() {
        this.reservas = new RepositorioEmMemoria<>();
    }

    public Reserva criarReserva(Participante participante, Quadra quadra, PeriodoReserva periodo, Modalidade modalidade, PoliticaCancelamento politicaCancelamento) {
        Reserva novaReserva  = new Reserva(participante,quadra,periodo,modalidade, politicaCancelamento);
        encontrarConflito(quadra, periodo).ifPresent(existente -> {
            throw new ConflitoReservaException(existente.getId());
        });
        reservas.adicionar(novaReserva);
        return novaReserva;
    }

    public List<Reserva> getReservas(){
        return reservas.listar();
    }

    public Reserva buscarReservaPorId(String id) {
        return reservas.obterPorId(id);
    }

    public void cancelarReserva(String id) {
        buscarReservaPorId(id).cancelar();
    }

    public void concluirReserva(String id) {
        buscarReservaPorId(id).concluir();
    }

    public List<Reserva> buscaPorQuadra(Quadra quadra){
        validarObrigatorio(quadra, "Quadra");
        return reservas.listar().stream()
                .filter(reserva -> reserva.getQuadra().equals(quadra))
                .toList();
    }

    public List<Reserva> buscaPorParticipante(Participante participante){
        validarObrigatorio(participante, "Participante");
        return reservas.listar().stream()
                .filter(reserva -> reserva.getParticipante().equals(participante))
                .toList();

    }

    public List<Reserva> buscaPorStatus(StatusReserva status){
        validarObrigatorio(status, "Status");
        return reservas.listar().stream()
                .filter(reserva -> reserva.getStatusReserva() == status)
                .toList();
    }

    public List<Reserva> reservasOrdenadasPorData(){
        return reservas.listar().stream()
                .sorted(Comparator.comparing(reserva -> reserva.getPeriodoReserva().inicio()))
                .toList();
    }

    /** Inclui o histórico: qualquer reserva cujo intervalo intercepte o período. */
    public List<Reserva> buscaPorPeriodo(PeriodoReserva periodo) {
        validarObrigatorio(periodo, "Período");
        return reservas.listar().stream()
                .filter(reserva -> reserva.getPeriodoReserva().sobrepoe(periodo))
                .toList();
    }

    public boolean estaDisponivel(Quadra quadra, PeriodoReserva periodo, Modalidade modalidade) {
        validarObrigatorio(quadra, "Quadra");
        validarObrigatorio(periodo, "Período");
        validarObrigatorio(modalidade, "Modalidade");
        return quadra.isAtiva() && quadra.aceitaModalidade(modalidade)
                && encontrarConflito(quadra, periodo).isEmpty();
    }

    private Optional<Reserva> encontrarConflito(Quadra quadra, PeriodoReserva periodo) {
        return reservas.listar().stream()
                .filter(reserva -> reserva.getStatusReserva() != StatusReserva.CANCELADA)
                .filter(reserva -> reserva.getQuadra().equals(quadra))
                .filter(reserva -> reserva.getPeriodoReserva().sobrepoe(periodo))
                .findFirst();
    }

    private static void validarObrigatorio(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " não pode ser nulo");
        }
    }
}
