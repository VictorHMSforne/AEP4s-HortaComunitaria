package br.edu.unicesumar;

public class Terreno {
    private int id;
    private String endereco;
    private double area;
    private boolean disponivel=true;
    private Bairro bairro;

    public Terreno(int id, String endereco, double area) {
        this.id = id;
        this.endereco = endereco;
        this.area = area;
    }

    public void setBairro(Bairro bairro){
        this.bairro = bairro;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getEndereco() {
        return endereco;
    }

    public double getArea() {
        return area;
    }
}
