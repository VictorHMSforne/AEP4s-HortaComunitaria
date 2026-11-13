package br.edu.unicesumar;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RelatorioController {
    private final JdbcTemplate banco;

    public RelatorioController(JdbcTemplate banco) {
        this.banco = banco;
    }

    @GetMapping("/api/relatorios/terrenos-por-bairro")
    public List<TerrenosPorBairro> terrenosPorBairro() {
        return banco.query("""
                SELECT b.id, b.nome, COUNT(t.id) AS cadastrados,
                       COUNT(t.id) FILTER (WHERE h.id IS NULL) AS baldios,
                       COUNT(t.id) FILTER (WHERE h.id IS NULL AND t.status = 'DISPONIVEL'
                            AND t.situacao_legal = 'APTO') AS aptos
                FROM bairro b
                LEFT JOIN terreno t ON t.bairro_id = b.id
                LEFT JOIN horta h ON h.terreno_id = t.id
                GROUP BY b.id, b.nome
                ORDER BY b.nome, b.id
                """, (linha, numero) -> new TerrenosPorBairro(linha.getLong("id"), linha.getString("nome"),
                linha.getLong("cadastrados"), linha.getLong("baldios"), linha.getLong("aptos")));
    }

    public record TerrenosPorBairro(long bairroId, String bairro, long terrenosCadastrados,
                                  long terrenosBaldios, long terrenosAptos) {}
}
