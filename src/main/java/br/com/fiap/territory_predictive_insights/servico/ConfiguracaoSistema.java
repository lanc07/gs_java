package br.com.fiap.territory_predictive_insights.servico;

import br.com.fiap.territory_predictive_insights.dominio.DadosTerritorio;

public class ConfiguracaoSistema {
    private static final ConfiguracaoSistema INSTANCIA = new ConfiguracaoSistema();

    private final DadosTerritorio dados = new DadosTerritorio("1306");
    private final int janela = 3;

    private ConfiguracaoSistema() { }

    public static ConfiguracaoSistema getInstance() { return INSTANCIA; }
    public DadosTerritorio getDados() { return dados; }
    public int getJanela() { return janela; }
}
