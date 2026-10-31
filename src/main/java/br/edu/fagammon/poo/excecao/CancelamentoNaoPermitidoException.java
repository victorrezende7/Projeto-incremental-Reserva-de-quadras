package br.edu.fagammon.poo.excecao;

public class CancelamentoNaoPermitidoException extends IllegalStateException {
    public CancelamentoNaoPermitidoException(String mensagem) {
        super(mensagem);
    }
}
