package br.edu.unicesumar;

import java.util.ArrayList;

import java.time.LocalDate;

public class Prefeitura extends Municipio{
    private int id;
    private String nomepref;
    private String cpnj;
    private ArrayList<Terreno> terrenos = new ArrayList<>();
    private ArrayList<Contrato> contratos = new ArrayList<>();

    public Prefeitura(String estado, String cep, String nome, int id, String nomepref, String cpnj) {
        super(estado, cep, nome);
        this.id = id;
        this.nomepref = nomepref;
        this.cpnj = cpnj;
    }

    public void addTerreno(Terreno terreno){
        this.terrenos.add(terreno);
    }

    public void firmarContrato(Proprietario proprietario){
        if(proprietario != null){
            Contrato c1 = new Contrato(1, LocalDate.of(2026,02,1),LocalDate.of(2027, 02, 1), 15000, 6);
            addContrato(c1);
        }
    }

    public void addContrato(Contrato contrato){
        this.contratos.add(contrato);
    }

    public String getNomepref() {
        return nomepref;
    }
}
