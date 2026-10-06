package br.edu.fagammon.poo.dominio;

import java.util.Objects;

public class Participante {
    private final String idParticipante;
    private final String nome;

    public Participante(String idParticipante, String nome) {
        if (idParticipante == null || idParticipante.isBlank()) {
            throw new IllegalArgumentException("ID não pode ser nulo ou vazio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        this.idParticipante = idParticipante;
        this.nome = nome;
    }

    public String  getId() {
        return idParticipante;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()) return false;
        Participante participante = (Participante) o;
        return Objects.equals(idParticipante, participante.idParticipante);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(idParticipante);
    }

}

