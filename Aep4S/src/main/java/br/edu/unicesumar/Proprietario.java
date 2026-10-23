package br.edu.unicesumar;

import java.util.ArrayList;

public class Proprietario extends Pessoa{
    private String telefone;
    private String email;
    private ArrayList<Terreno> terrenos= new ArrayList<>();

    public Proprietario(String nome, String cpf, String endereco, String telefone, String email) {
        super(nome, cpf, endereco);
        this.telefone = telefone;
        this.email = email;
    }

    public void addTerreno(Terreno terreno){
        this.terrenos.add(terreno);
        terreno.setProprietario(this);
    }
}
