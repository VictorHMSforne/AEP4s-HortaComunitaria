package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "prefeitura")
public class Prefeitura extends Registro {
    @NotBlank @Size(max = 160)
    private String nome;

    @NotBlank @Pattern(regexp = "[0-9]{14}")
    private String cnpj;
}
