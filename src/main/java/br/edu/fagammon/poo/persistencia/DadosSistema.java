
package br.edu.fagammon.poo.persistencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record DadosSistema(
        List<DadosParticipante> participantes,
        List<DadosQuadra> quadras,
        List<DadosReserva> reservas
) {

    public record DadosParticipante(
            String id,
            String nome
    ) {}

    public record DadosQuadra(
            String id,
            String nome,
            Set<String> modalidades,
            boolean ativa
    ) {}

    public record DadosReserva(
            String id,
            String participanteId,
            String quadraId,
            String inicio,
            String fim,
            String modalidade,
            String status,
            BigDecimal preco,
            String politicaCancelamento,
            Long antecedenciaMinutos
    ) {}
}
