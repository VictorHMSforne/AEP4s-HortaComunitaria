package br.edu.unicesumar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CadastroController {
    private static final Map<String, Class<? extends Registro>> RECURSOS = Map.ofEntries(
            Map.entry("bairros", Bairro.class), Map.entry("prefeituras", Prefeitura.class),
            Map.entry("proprietarios", Proprietario.class), Map.entry("gestores", GestorHorta.class),
            Map.entry("terrenos", Terreno.class), Map.entry("contratos", Contrato.class),
            Map.entry("hortas", Horta.class), Map.entry("familias", Familia.class),
            Map.entry("membros", MembroFamilia.class), Map.entry("colheitas", ProducaoHorta.class),
            Map.entry("minhocarios", Minhocario.class), Map.entry("distribuicoes", Distribuicao.class)
    );

    private final CadastroService cadastro;
    private final Validator validator;
    private final ObjectMapper mapper;

    public CadastroController(CadastroService cadastro, Validator validator, ObjectMapper mapper) {
        this.cadastro = cadastro;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping("/{recurso}")
    public List<? extends Registro> listar(@PathVariable String recurso) {
        return cadastro.listar(tipo(recurso));
    }

    @GetMapping("/{recurso}/{id}")
    public Registro buscar(@PathVariable String recurso, @PathVariable long id) {
        return cadastro.buscar(tipo(recurso), id);
    }

    @PostMapping("/{recurso}")
    public ResponseEntity<Registro> criar(@PathVariable String recurso, @RequestBody JsonNode json) {
        Registro registro = cadastro.criar(ler(tipo(recurso), json));
        return ResponseEntity.created(URI.create("/api/" + recurso + "/" + registro.getId())).body(registro);
    }

    @PutMapping("/{recurso}/{id}")
    public Registro atualizar(@PathVariable String recurso, @PathVariable long id, @RequestBody JsonNode json) {
        return atualizar(tipo(recurso), id, json);
    }

    @DeleteMapping("/{recurso}/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String recurso, @PathVariable long id) {
        cadastro.excluir(tipo(recurso), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hortas/{id}/producoes")
    public List<Producao> producoes(@PathVariable long id) {
        return cadastro.producoes(id);
    }

    private <T extends Registro> T atualizar(Class<T> tipo, long id, JsonNode json) {
        return cadastro.atualizar(tipo, id, ler(tipo, json));
    }

    private Class<? extends Registro> tipo(String recurso) {
        Class<? extends Registro> classe = RECURSOS.get(recurso);
        if (classe == null) {
            throw new ErroCadastro(404, "Cadastro não encontrado.");
        }
        return classe;
    }

    private <T extends Registro> T ler(Class<T> tipo, JsonNode json) {
        if (!json.isObject()) {
            throw new ErroCadastro(400, "Envie um objeto JSON com os dados do cadastro.");
        }
        T registro;
        try {
            registro = mapper.convertValue(json, tipo);
        } catch (IllegalArgumentException e) {
            throw new ErroCadastro(400, "Há um campo desconhecido ou com formato inválido.");
        }
        var erros = validator.validate(registro);
        if (!erros.isEmpty()) {
            String mensagem = erros.stream()
                    .map(erro -> erro.getPropertyPath() + " " + erro.getMessage())
                    .sorted()
                    .collect(Collectors.joining(" / "));
            throw new ErroCadastro(400, mensagem);
        }
        return registro;
    }
}
