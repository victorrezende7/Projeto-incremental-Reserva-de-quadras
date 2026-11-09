package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.Modalidade;
import br.edu.fagammon.poo.dominio.PeriodoReserva;
import br.edu.fagammon.poo.dominio.Reserva;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public class PrecoPorModalidade implements PoliticaPreco{
    private final BigDecimal precoPadrao;
    private final Map<Modalidade,BigDecimal> precosEspeciais;

    public PrecoPorModalidade(BigDecimal precoPadrao, Map<Modalidade,BigDecimal> precosEspeciais) {
        if(precoPadrao == null || precoPadrao.compareTo(BigDecimal.ZERO) <= 0 ) throw new IllegalArgumentException("O preço padrão não pode ser nulo ou 0");
        if(precosEspeciais == null) throw new IllegalArgumentException("Preços especiais não podem ser nulos");

        for (Map.Entry<Modalidade, BigDecimal> entrada : precosEspeciais.entrySet()) {
            Modalidade modalidade = entrada.getKey();
            BigDecimal preco = entrada.getValue();

            if (modalidade == null) {
                throw new IllegalArgumentException("A modalidade não pode ser nula");
            }

            if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "O preço da modalidade deve ser maior que zero"
                );
            }
        }



        this.precoPadrao = precoPadrao;

        this.precosEspeciais = new EnumMap<>(Modalidade.class);
        this.precosEspeciais.putAll(precosEspeciais);
    }

    @Override
    public BigDecimal calcularPreco(PeriodoReserva periodo, Modalidade modalidade) {
        if (periodo == null || modalidade == null) {
            throw new IllegalArgumentException("Periodo ou modalidade não podem ser nulos");
        }
        BigDecimal valorPorHora = precosEspeciais.getOrDefault(
                modalidade,
                precoPadrao
        );

        LocalDateTime inicio = periodo.inicio();
        LocalDateTime fim = periodo.fim();
        Duration duracao = Duration.between(inicio,fim);
        long minutos = duracao.toMinutes();

        BigDecimal precoFinal = BigDecimal.valueOf(minutos)
                .multiply(valorPorHora)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        return precoFinal;

    }
}
