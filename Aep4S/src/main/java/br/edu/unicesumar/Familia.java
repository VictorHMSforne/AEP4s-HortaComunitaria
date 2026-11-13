package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "familia")
public class Familia extends Registro {
    @NotBlank @Size(max = 160)
    private String nome;

    @NotNull @Positive
    private Long hortaId;

    public Long getHortaId() {
        return hortaId;
    }
}
