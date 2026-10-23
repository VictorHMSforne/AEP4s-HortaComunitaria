package br.edu.unicesumar;

public class Terreno implements MetodosPadroes{
    private int id;
    private String endereco;
    private double area;
    private boolean disponivel=true;
    private Bairro bairro;
    private Proprietario proprietario;
    private Prefeitura prefeitura;

    public Terreno(int id, String endereco, double area, Prefeitura prefeitura) {
        this.id = id;
        this.endereco = endereco;
        this.area = area;
        this.prefeitura = prefeitura;
        prefeitura.addTerreno(this);
    }

    public void setProprietario(Proprietario proprietario){
        this.proprietario = proprietario;
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

    public Proprietario getProprietario(){
        return proprietario;
    }

    @Override
    public void exibirDados(){
        if(getProprietario() != null){
            System.out.printf("Nome proprietário: %s", getProprietario().getNome());
            System.out.println();
            System.out.printf("Endereço proprietário: %s", getProprietario().getEndereco());
            System.out.println();
            System.out.printf("CPF proprietário: %s", getProprietario().getCpf());
            System.out.println();
        }else{
            System.out.printf("O terreno ainda não está vinculado a um proprietário");
            System.out.println();
        }
        System.out.printf("Endereço: %s" ,endereco);
        System.out.println();
        System.out.printf("Area: %.2f", area);
        System.out.println();
        System.out.printf("Disponível: %s", disponivel ? "sim" : "nao");
        System.out.println();
        System.out.printf("Nome prefeitura que o terreno está vinculado: %s", prefeitura.getNomepref());
    }
}
