package br.edu.unicesumar;

import java.util.ArrayList;

public class Familia {
    private int id;
    private ArrayList<Pessoa> pessoas;

    public Familia(int id) {
        this.id = id;
        this.pessoas = new ArrayList<>();
    }

    public ArrayList<Pessoa> getPessoas() {
        return pessoas;
    }

    public void addPessoa(Pessoa pessoa){
        this.pessoas.add(pessoa);
    }

    public void removePessoa(int i){
        this.pessoas.remove(i);
    }
}
