package br.edu.fagammon.poo.dominio;

import java.time.LocalDateTime;

public record PeriodoReserva(LocalDateTime inicio, LocalDateTime fim) {
    public PeriodoReserva{
        if(inicio == null || fim == null){
            throw new IllegalArgumentException("O Início e fim são obrigatórios");
        }
        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("Início do período deve ser anterior ao fim");
        }
    }

    public boolean sobrepoe(PeriodoReserva outro){
        if (outro == null) {
            throw new IllegalArgumentException("Período para comparação não pode ser nulo");
        }
        return this.inicio.isBefore(outro.fim) && outro.inicio.isBefore(this.fim);
    }

}
