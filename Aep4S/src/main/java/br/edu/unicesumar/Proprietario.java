package br.edu.unicesumar;

public class Proprietario extends Pessoa{
    private String telefone;
    private String email;

    public Proprietario(String nome, String cpf, String endereco, String telefone, String email) {
        super(nome, cpf, endereco);
        this.telefone = telefone;
        this.email = email;
    }
}
