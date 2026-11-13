package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "distribuicao")
public class Distribuicao extends Registro {
    @NotNull @Positive
    private Long colheitaId;

    @NotNull @Positive
    private Long familiaId;

    @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction = 2)
    private BigDecimal quantidade;

    @NotNull
    private LocalDate dataEntrega;

    public Long getColheitaId() {
        return colheitaId;
    }

    public Long getFamiliaId() {
        return familiaId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }
}
