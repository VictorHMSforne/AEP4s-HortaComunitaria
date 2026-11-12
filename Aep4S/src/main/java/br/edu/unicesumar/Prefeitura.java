package br.edu.unicesumar;

import java.util.ArrayList;

import java.time.LocalDate;

public class Prefeitura {
    private int id;
    private String nome;
    private String cpnj;
    private ArrayList<Terreno> terrenos = new ArrayList<>();
    private ArrayList<Contrato> contratos = new ArrayList<>();

    public Prefeitura(int id, String nome, String cpnj) {
        this.id = id;
        this.nome = nome;
        this.cpnj = cpnj;
    }

    public void addTerreno(Terreno terreno){
        this.terrenos.add(terreno);
    }

    public void firmarContrato(Proprietario proprietario){
        if(proprietario != null){
            Contrato c1 = new Contrato(1, LocalDate.of(2026,02,1),LocalDate.of(2027, 02, 1), 15000, 6);
            addContrato(c1,proprietario);
        }
    }

    public void addContrato(Contrato contrato, Proprietario proprietario){
        contratos.add(contrato);
        proprietario.addContrato(contrato);
    }

    public String getNome() {
        return nome;
    }
}
