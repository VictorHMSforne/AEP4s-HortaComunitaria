package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "terreno")
public class Terreno extends Registro {
    @NotBlank @Size(max = 240)
    private String endereco;

    @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction = 2)
    private BigDecimal area;

    @NotNull @Positive
    private Long bairroId;

    @NotNull @Positive
    private Long prefeituraId;

    @Positive
    private Long proprietarioId;

    @NotNull @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        DISPONIVEL, INDISPONIVEL
    }

    @NotNull @Enumerated(EnumType.STRING)
    private SituacaoLegal situacaoLegal;

    public enum SituacaoLegal {
        A_ANALISAR, APTO, INAPTO
    }

    public Long getPrefeituraId() {
        return prefeituraId;
    }

    public Status getStatus() {
        return status;
    }

    public SituacaoLegal getSituacaoLegal() {
        return situacaoLegal;
    }
}
