package br.edu.unicesumar;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class TratamentoErros {
    @ExceptionHandler(ErroCadastro.class)
    public ResponseEntity<Map<String, String>> cadastro(ErroCadastro erro) {
        return resposta(erro.getStatus(), erro.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridade(DataIntegrityViolationException erro) {
        String codigo = erro.getMostSpecificCause() instanceof java.sql.SQLException sql ? sql.getSQLState() : null;
        return integridade(codigo);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> restricao(ConstraintViolationException erro) {
        return integridade(erro.getSQLState());
    }

    private ResponseEntity<Map<String, String>> integridade(String codigo) {
        return switch (Objects.requireNonNullElse(codigo, "")) {
            case "23505" -> resposta(409, "Já existe um cadastro com estes dados únicos.");
            case "23503" -> resposta(409, "Verifique os vínculos. Um registro informado não existe, não corresponde ao vínculo exigido ou ainda é utilizado.");
            default -> resposta(400, "Os dados violam uma regra de integridade.");
        };
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<Map<String, String>> concorrencia() {
        return resposta(409, "O registro está sendo alterado. Tente novamente.");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> formato() {
        return resposta(400, "O formato do pedido ou do identificador é inválido.");
    }

    private ResponseEntity<Map<String, String>> resposta(int status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
