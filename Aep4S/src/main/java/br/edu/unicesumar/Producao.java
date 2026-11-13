package br.edu.unicesumar;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "producao")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Producao extends Registro {
    @NotNull @Positive
    @Column(name = "horta_id")
    private Long hortaId;

    public Long getHortaId() {
        return hortaId;
    }

    @JsonProperty("rendimento")
    public abstract BigDecimal calcularRendimento();
}
