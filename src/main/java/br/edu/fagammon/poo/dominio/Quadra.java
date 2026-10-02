package br.edu.fagammon.poo.dominio;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Quadra {
    private final String id;
    private final String nome;
    private final Set<Modalidade> modalidadesPermitidas;
    private boolean ativa;


    public Quadra(String id, String nome, Set<Modalidade> modalidadesPermitidas){
        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Id da quadra não pode ser nulo ou vazio");
        }
        if(nome == null || nome.isBlank()){
            throw new IllegalArgumentException("Nome da quadra não pode ser nulo ou vazio");
        }
        if(modalidadesPermitidas == null || modalidadesPermitidas.isEmpty()){
            throw new IllegalArgumentException("Quadra precisa de ao menos uma modalidade");
        }

        this.id = id;
        this.nome = nome;
        this.modalidadesPermitidas = EnumSet.copyOf(modalidadesPermitidas);
        this.ativa = true;
    }


    public String getId(){return id;}
    public String getNome(){return nome;}


    public boolean aceitaModalidade(Modalidade modalidade){
        return (modalidadesPermitidas.contains(modalidade));
    }

    public void ativar(){
        this.ativa = true;
    }


    public void desativar(){
        this.ativa = false;
    }

    public boolean isAtiva(){
        return ativa;
    }

    public Set<Modalidade> getModalidadesPermitidas() {
        return EnumSet.copyOf(modalidadesPermitidas);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Quadra quadra = (Quadra) o;
        return Objects.equals(id, quadra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
