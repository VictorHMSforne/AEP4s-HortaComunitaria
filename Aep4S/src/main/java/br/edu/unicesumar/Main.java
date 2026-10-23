package br.edu.unicesumar;

public class Main {
    public static void main(String[] args) {

        Proprietario matheus = new Proprietario ("Matheus", "49032694855", "Rua osvaldo Volpato", "44988290951", "matheusrochaa063@gmail.com");

        Bairro b1 = new Bairro(1, "Jardim dos Palmares");

        Terreno t1 = new Terreno(1, "Rua das camelias", 50.3);

        b1.addTerreno(t1);
        b1.exibirDados();
    }
}
