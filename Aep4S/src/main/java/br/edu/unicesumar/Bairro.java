package br.edu.unicesumar;

import java.util.ArrayList;

public class Bairro implements MetodosPadroes {
    private int id;
    private String nome;
    private ArrayList<Terreno> terrenos = new ArrayList<>();

    public Bairro(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public void addTerreno(Terreno terreno){
        this.terrenos.add(terreno);
        terreno.setBairro(this);
    }

    public void removeTerreno(Terreno terreno){
        this.terrenos.remove(terreno);
        terreno.setBairro(null);
    }

    @Override
    public void exibirDados(){
        System.out.printf("Nome Bairro %s", nome);
        System.out.println();

        for(Terreno terreno: terrenos){
            System.out.printf("Endereço: %s", terreno.getEndereco());
            System.out.println();
            System.out.printf("Area %.2f", terreno.getArea());
        }
    }
}
