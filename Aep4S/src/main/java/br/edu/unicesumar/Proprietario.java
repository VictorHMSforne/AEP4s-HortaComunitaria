package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proprietario")
public class Proprietario extends Pessoa {
    @NotBlank @Size(max = 30)
    private String telefone;

    @NotBlank @Email @Size(max = 160)
    private String email;
}
