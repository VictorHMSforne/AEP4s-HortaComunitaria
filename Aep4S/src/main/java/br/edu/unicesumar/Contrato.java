package br.edu.unicesumar;

import java.time.LocalDate;

public class Contrato {
    private int id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valorContrato;
    private double percentualProducao;

    public Contrato(int id, LocalDate dataInicio, LocalDate dataFim, double valorContrato, double percentualProducao) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorContrato = valorContrato;
        this.percentualProducao = percentualProducao;
    }
}
