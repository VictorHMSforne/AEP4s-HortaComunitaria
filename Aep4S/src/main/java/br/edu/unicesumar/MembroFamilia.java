package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "membro_familia")
public class MembroFamilia extends Pessoa {
    @NotNull @Positive
    private Long familiaId;
}
