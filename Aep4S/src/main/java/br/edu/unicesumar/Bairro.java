package br.edu.unicesumar;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Bairro {
    private int id;
    private String nome;
    private ArrayList<Terreno> terrenos;

    public Bairro(int id, String nome, ArrayList<Terreno> terrenos) {
        this.id = id;
        this.nome = nome;
        this.terrenos = terrenos;
    }

    public void addTerreno(Terreno terreno){
        this.terrenos.add(terreno);
    }

    public void removeTerreno(int i){
        this.terrenos.remove(i);
    }
}
