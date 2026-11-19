package br.com.fiap.territory_predictive_insights.dominio;

public enum NivelAlerta {
    ATENCAO("Fique de olho: a demanda está chegando perto do limite"),
    ALERTA("Considere reforçar o atendimento: o limite deve ser ultrapassado"),
    CRITICO("Reforce a equipe agora: o limite deve ser ultrapassado com folga");

    private final String orientacao;

    NivelAlerta(String orientacao) { this.orientacao = orientacao; }

    public String getOrientacao() { return orientacao; }
}