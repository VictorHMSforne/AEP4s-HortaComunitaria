package br.edu.unicesumar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "producao_horta")
public class ProducaoHorta extends Producao {
    @NotBlank @Size(max = 120)
    private String produto;

    @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction = 2)
    private BigDecimal quantidadeProduzida;

    @NotNull
    private LocalDate dataPlantio;

    @NotNull
    private LocalDate dataColheita;

    public LocalDate getDataColheita() {
        return dataColheita;
    }

    @AssertTrue(message = "A colheita deve ocorrer na data de plantio ou depois.")
    @JsonIgnore
    public boolean isDatasValidas() {
        return dataPlantio == null || dataColheita == null || !dataColheita.isBefore(dataPlantio);
    }

    @Override
    public BigDecimal calcularRendimento() {
        return quantidadeProduzida;
    }
}
