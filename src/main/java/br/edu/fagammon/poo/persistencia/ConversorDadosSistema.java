
package br.edu.fagammon.poo.persistencia;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.*;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConversorDadosSistema {

    public DadosSistema paraDados(List<Reserva> reservas) {
        if (reservas == null) {
            throw new IllegalArgumentException("Lista de reservas nula");
        }

        Map<String, Participante> participantes = new LinkedHashMap<>();
        Map<String, Quadra> quadras = new LinkedHashMap<>();
        List<DadosSistema.DadosReserva> dadosReservas = new ArrayList<>();

        for (Reserva reserva : reservas) {
            if (reserva == null) {
                throw new IllegalArgumentException("Reserva nula");
            }

            Participante participante = reserva.getParticipante();
            Quadra quadra = reserva.getQuadra();

            participantes.putIfAbsent(participante.getId(), participante);
            quadras.putIfAbsent(quadra.getId(), quadra);

            String tipoPolitica;
            Long antecedencia = null;

            if (reserva.getPoliticaCancelamento() instanceof CancelamentoLivre) {
                tipoPolitica = "LIVRE";

            } else if (reserva.getPoliticaCancelamento()
                    instanceof CancelamentoComAntecedencia politica) {

                tipoPolitica = "ANTECEDENCIA";
                Duration duracao = politica.getAntecedenciaMinima();

                if (duracao.getSeconds() % 60 != 0 || duracao.getNano() != 0) {
                    throw new IllegalArgumentException(
                            "Antecedência deve ser em minutos inteiros"
                    );
                }

                antecedencia = duracao.toMinutes();

            } else {
                throw new IllegalArgumentException(
                        "Política de cancelamento não suportada"
                );
            }

            dadosReservas.add(new DadosSistema.DadosReserva(
                    reserva.getId(),
                    participante.getId(),
                    quadra.getId(),
                    reserva.getPeriodoReserva().inicio().toString(),
                    reserva.getPeriodoReserva().fim().toString(),
                    reserva.getModalidade().name(),
                    reserva.getStatusReserva().name(),
                    reserva.getPreco(),
                    tipoPolitica,
                    antecedencia
            ));
        }

        List<DadosSistema.DadosParticipante> dadosParticipantes =
                participantes.values().stream()
                        .map(p -> new DadosSistema.DadosParticipante(
                                p.getId(), p.getNome()
                        ))
                        .toList();

        List<DadosSistema.DadosQuadra> dadosQuadras =
                quadras.values().stream()
                        .map(q -> new DadosSistema.DadosQuadra(
                                q.getId(),
                                q.getNome(),
                                q.getModalidadesPermitidas().stream()
                                        .map(Enum::name)
                                        .collect(Collectors.toSet()),
                                q.isAtiva()
                        ))
                        .toList();

        return new DadosSistema(
                dadosParticipantes,
                dadosQuadras,
                dadosReservas
        );
    }

    public List<Reserva> paraReservas(DadosSistema dados) {
        if (dados == null || dados.participantes() == null
                || dados.quadras() == null || dados.reservas() == null) {
            throw new IllegalArgumentException("Dados incompletos");
        }

        Map<String, Participante> participantes = new HashMap<>();

        for (DadosSistema.DadosParticipante p : dados.participantes()) {
            Participante participante = new Participante(p.id(), p.nome());

            if (participantes.putIfAbsent(p.id(), participante) != null) {
                throw new IllegalArgumentException("Participante duplicado");
            }
        }

        Map<String, Quadra> quadras = new HashMap<>();

        for (DadosSistema.DadosQuadra q : dados.quadras()) {
            Set<Modalidade> modalidades = EnumSet.noneOf(Modalidade.class);

            for (String nome : q.modalidades()) {
                modalidades.add(Modalidade.valueOf(nome));
            }

            Quadra quadra = new Quadra(q.id(), q.nome(), modalidades);

            if (!q.ativa()) {
                quadra.desativar();
            }

            if (quadras.putIfAbsent(q.id(), quadra) != null) {
                throw new IllegalArgumentException("Quadra duplicada");
            }
        }

        List<Reserva> reservas = new ArrayList<>();
        Set<String> idsReservas = new HashSet<>();

        for (DadosSistema.DadosReserva r : dados.reservas()) {
            Participante participante = participantes.get(r.participanteId());
            Quadra quadra = quadras.get(r.quadraId());

            if (participante == null || quadra == null) {
                throw new IllegalArgumentException(
                        "Participante ou quadra não encontrado"
                );
            }

            PoliticaCancelamento politica;

            if ("LIVRE".equals(r.politicaCancelamento())) {
                politica = new CancelamentoLivre();

            } else if ("ANTECEDENCIA".equals(r.politicaCancelamento())) {
                if (r.antecedenciaMinutos() == null) {
                    throw new IllegalArgumentException(
                            "Antecedência não informada"
                    );
                }

                politica = new CancelamentoComAntecedencia(
                        Duration.ofMinutes(r.antecedenciaMinutos()),
                        Clock.systemDefaultZone()
                );

            } else {
                throw new IllegalArgumentException(
                        "Política desconhecida: " + r.politicaCancelamento()
                );
            }

            PeriodoReserva periodo = new PeriodoReserva(
                    LocalDateTime.parse(r.inicio()),
                    LocalDateTime.parse(r.fim())
            );

            Reserva reserva = Reserva.restaurar(
                    r.id(),
                    participante,
                    quadra,
                    periodo,
                    Modalidade.valueOf(r.modalidade()),
                    politica,
                    r.preco(),
                    StatusReserva.valueOf(r.status())
            );

            if (!idsReservas.add(reserva.getId())) {
                throw new IllegalArgumentException("ID de reserva duplicado");
            }

            boolean conflito = reservas.stream().anyMatch(existente ->
                    existente.getStatusReserva() != StatusReserva.CANCELADA
                            && reserva.getStatusReserva() != StatusReserva.CANCELADA
                            && existente.getQuadra().equals(reserva.getQuadra())
                            && existente.getPeriodoReserva()
                            .sobrepoe(reserva.getPeriodoReserva())
            );

            if (conflito) {
                throw new IllegalArgumentException(
                        "Conflito entre reservas restauradas"
                );
            }

            reservas.add(reserva);
        }

        return List.copyOf(reservas);
    }
}
