package br.edu.unicesumar;

import java.util.ArrayList;

public class Familia {
    private int id;
    private ArrayList<Pessoa> pessoas = new ArrayList<>();;

    public Familia(int id) {
        this.id = id;
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