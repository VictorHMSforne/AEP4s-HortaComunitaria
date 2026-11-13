package br.edu.unicesumar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "bairro")
public class Bairro extends Registro {
    @NotBlank @Size(max = 120)
    private String nome;
}
