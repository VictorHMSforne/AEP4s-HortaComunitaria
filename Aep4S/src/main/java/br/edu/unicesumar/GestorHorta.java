package br.edu.unicesumar;

public class GestorHorta extends Pessoa{
    private int matricula;
    private String dataContrato;
    private double ajudaCusto;

    public GestorHorta(String nome, String cpf, String endereco, int matricula, String dataContrato, double ajudaCusto) {
        super(nome, cpf, endereco);
        this.matricula = matricula;
        this.dataContrato = dataContrato;
        this.ajudaCusto = ajudaCusto;
    }
}