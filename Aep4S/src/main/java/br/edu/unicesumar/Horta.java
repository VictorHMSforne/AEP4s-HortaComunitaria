package br.edu.unicesumar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "horta")
public class Horta extends Registro {
    @NotBlank @Size(max = 160)
    private String nome;

    @NotNull @Positive
    private Long terrenoId;

    @NotNull @Positive
    private Long gestorId;

    @OneToMany
    @JoinColumn(name = "horta_id", insertable = false, updatable = false)
    @OrderBy("id")
    @JsonIgnore
    private List<Producao> producoes = new ArrayList<>();

    void atualizarDados(Horta dados) {
        nome = dados.nome;
        terrenoId = dados.terrenoId;
        gestorId = dados.gestorId;
    }

    public Long getTerrenoId() {
        return terrenoId;
    }

    public Long getGestorId() {
        return gestorId;
    }

    public List<Producao> getProducoes() {
        return List.copyOf(producoes);
    }
}
