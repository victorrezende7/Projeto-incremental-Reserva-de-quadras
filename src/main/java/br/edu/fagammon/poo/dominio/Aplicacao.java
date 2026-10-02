package br.edu.fagammon.poo.dominio;

import java.util.EnumSet;
import java.util.Set;

public class Aplicacao {
    static void main(String[] args) {
        Quadra quadra1 = new Quadra("1", "Q1", EnumSet.of(Modalidade.FUTSAL, Modalidade.VOLEIBOL, Modalidade.SOCIETY,Modalidade.FUTSAL));
        Quadra quadra2 = new Quadra("1", "Q3", EnumSet.of(Modalidade.SOCIETY,Modalidade.FUTSAL));

        System.out.println(quadra1 == quadra2);
        System.out.println(quadra1.equals(quadra2));
    }
}
