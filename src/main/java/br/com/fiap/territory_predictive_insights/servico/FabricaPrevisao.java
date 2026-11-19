package br.com.fiap.territory_predictive_insights.servico;

import br.com.fiap.territory_predictive_insights.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory_predictive_insights.dominio.EstrategiaPrevisao;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights.dominio.PrevisaoComparacao;

import java.util.Map;
import java.util.function.IntFunction;

public class FabricaPrevisao {
    private static final Map<String, IntFunction<EstrategiaPrevisao>> CATALOGO = Map.of(
            "MEDIA_MOVEL", PrevisaoMediaMovel::new,
            "COMPARACAO", PrevisaoComparacao::new);

    private FabricaPrevisao() { }

    public static EstrategiaPrevisao criar(String tipo, int janela) {
        IntFunction<EstrategiaPrevisao> construtor = CATALOGO.get(tipo);
        if (construtor == null) throw new DadosInvalidosException("Estratégia desconhecida: " + tipo);
        return construtor.apply(janela);
    }
}
