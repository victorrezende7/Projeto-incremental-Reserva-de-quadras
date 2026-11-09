package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.Modalidade;
import br.edu.fagammon.poo.dominio.PeriodoReserva;
import br.edu.fagammon.poo.dominio.Reserva;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrecoFixoPorHora implements PoliticaPreco {
    private final BigDecimal valorPorHora;

    public PrecoFixoPorHora(BigDecimal valorPorHora){
        if(valorPorHora == null) throw new IllegalArgumentException("O valor por hora não pode ser nulo");
        if(valorPorHora.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("O valor por hora não pode ser menor ou igual a zero");

        this.valorPorHora = valorPorHora;

    }

    @Override
    public BigDecimal calcularPreco(PeriodoReserva periodo, Modalidade modalidade) {
        if((periodo == null || modalidade == null)) throw new IllegalArgumentException("Periodo ou modalidade não podem ser nulos");
        LocalDateTime inicio = periodo.inicio();
        LocalDateTime fim = periodo.fim();
        Duration duracao = Duration.between(inicio,fim);
        long minutos = duracao.toMinutes();
        BigDecimal precoFinal = BigDecimal.valueOf(minutos)
              .multiply(valorPorHora)
              .divide(BigDecimal.valueOf(60),2, RoundingMode.HALF_UP);
        return precoFinal;
    };
}
