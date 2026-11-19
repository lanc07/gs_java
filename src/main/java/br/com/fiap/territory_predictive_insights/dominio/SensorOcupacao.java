package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.FonteIndisponivelException;

import java.util.Random;

public class SensorOcupacao implements Sensor {
    private final Random random = new Random();

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de ocupação " +
                    "indisponível");
        }
        return random.nextInt(101);
    }
    @Override
    public boolean verificarFuncionamento() {
        return random.nextInt(100) >= 5; //força erro em 5% das vezes
    }
    @Override
    public String getTipo() {
        return "OCUPACAO";
    }
    @Override
    public String formatar(double valor) { return String.format("%.0f%% de ocupação", valor); }
}
