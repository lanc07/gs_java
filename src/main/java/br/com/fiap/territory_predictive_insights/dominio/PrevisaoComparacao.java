package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {
    private final int janela;

    public PrevisaoComparacao(int janela) {
        super("Comparação com período anterior");
        if (janela <= 0) throw new DadosInvalidosException("Janela deve ser positiva: " + janela);
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico, janela * 2);
        int n = historico.size();
        double recente = media(historico.subList(n - janela, n));
        double anterior = media(historico.subList(n - 2 * janela, n - janela));
        return Math.max(0, recente + (recente - anterior));   // projeta a tendência
    }

    private double media(List<Medicao> lista) {
        double soma = 0;
        for (Medicao m : lista) soma += m.getValor();
        return soma / lista.size();
    }
}
