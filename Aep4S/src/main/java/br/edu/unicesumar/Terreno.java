package br.edu.unicesumar;

public class Terreno {
    private int id;
    private String endereco;
    private double area;
    private boolean disponivel;

    public Terreno(int id, String endereco, double area, boolean disponivel) {
        this.id = id;
        this.endereco = endereco;
        this.area = area;
        this.disponivel=true;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
