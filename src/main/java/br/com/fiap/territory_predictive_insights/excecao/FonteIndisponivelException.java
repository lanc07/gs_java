package br.com.fiap.territory_predictive_insights.excecao;

public class FonteIndisponivelException extends RuntimeException {
    public FonteIndisponivelException (String mensagem) {
        super(mensagem);
    }
}
