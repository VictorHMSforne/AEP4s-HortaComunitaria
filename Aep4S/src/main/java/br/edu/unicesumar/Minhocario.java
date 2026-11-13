package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "minhocario")
public class Minhocario extends Producao {
    @NotNull @PositiveOrZero
    private Integer quantidadeMinhocas;

    @NotNull @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 10, fraction = 2)
    private BigDecimal quantidadeAdubo;

    @NotNull
    private LocalDate dataRegistro;

    @Override
    public BigDecimal calcularRendimento() {
        return quantidadeAdubo;
    }
}
