package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gestor_horta")
public class GestorHorta extends Pessoa {
    @NotBlank @Size(max = 40)
    private String matricula;

    @NotNull
    private LocalDate dataContratacao;

    @NotNull @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 10, fraction = 2)
    private BigDecimal ajudaCusto;

    @NotNull @Positive
    private Long prefeituraId;

    public Long getPrefeituraId() {
        return prefeituraId;
    }
}
