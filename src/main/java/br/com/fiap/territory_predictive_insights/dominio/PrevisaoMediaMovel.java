package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {
    private final int janela;

    public PrevisaoMediaMovel(int janela ){
        super("media movel");
        if (janela <= 0) throw new DadosInvalidosException("Janela deve ser " +
                "positiva: " + janela);
        this.janela = janela;
    }
    @Override
    public double prever(List<Medicao> historico ) {
        validarHistorico(historico, janela);
        List<Medicao> ultimas = historico.subList(historico.size() - janela,
                historico.size());
        double soma = 0;
        for(Medicao m : ultimas) soma += m.getValor();
        return soma/janela;
    }
}
