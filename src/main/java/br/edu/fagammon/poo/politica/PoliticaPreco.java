package br.edu.fagammon.poo.politica;

import br.edu.fagammon.poo.dominio.Modalidade;
import br.edu.fagammon.poo.dominio.PeriodoReserva;


import java.math.BigDecimal;

public interface PoliticaPreco {
    BigDecimal calcularPreco(PeriodoReserva periodo, Modalidade modalidade);
}
