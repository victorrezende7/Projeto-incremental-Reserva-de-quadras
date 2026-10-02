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
}
