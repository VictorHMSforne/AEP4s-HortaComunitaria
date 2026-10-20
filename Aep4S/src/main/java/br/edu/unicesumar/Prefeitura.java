package br.edu.unicesumar;

public class Prefeitura extends Municipio{
    private int id;
    private String nome;
    private String cpnj;

    public Prefeitura(String estado, String cep, String nome, int id, String nome1, String cpnj) {
        super(estado, cep, nome);
        this.id = id;
        this.nome = nome1;
        this.cpnj = cpnj;
    }

    public void contratarGestor(GestorHorta gestor){

    }
}
