package br.edu.unicesumar;

public class ErroCadastro extends RuntimeException {
    private final int status;

    public ErroCadastro(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
