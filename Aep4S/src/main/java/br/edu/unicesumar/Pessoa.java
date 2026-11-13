package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pessoa")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa extends Registro {
    @NotBlank @Size(max = 160)
    private String nome;

    @NotBlank @Pattern(regexp = "[0-9]{11}")
    private String cpf;

    @NotBlank @Size(max = 240)
    private String endereco;
}
