package br.edu.unicesumar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contrato")
public class Contrato extends Registro {
    @NotNull @Positive
    private Long terrenoId;

    @NotNull @Positive
    private Long proprietarioId;

    @NotNull @Positive
    private Long prefeituraId;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    @NotNull @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 10, fraction = 2)
    private BigDecimal valorAluguel;

    @NotNull @DecimalMin("0.00") @DecimalMax("100.00") @Digits(integer = 3, fraction = 2)
    private BigDecimal percentualProducao;

    @Size(max = 240)
    private String incentivoFiscal;

    @NotNull @Enumerated(EnumType.STRING)
    private TipoAdesao tipoAdesao;

    public enum TipoAdesao {
        LEGAL, VOLUNTARIA
    }

    @AssertTrue(message = "O fim do contrato deve ser igual ou posterior ao início.")
    @JsonIgnore
    public boolean isPeriodoValido() {
        return dataInicio == null || dataFim == null || !dataFim.isBefore(dataInicio);
    }
}
