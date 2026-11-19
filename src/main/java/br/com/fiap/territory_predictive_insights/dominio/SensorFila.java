package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.FonteIndisponivelException;

import java.util.Random;

public class SensorFila implements Sensor {
    private final Random random = new Random();

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de fila indisponível no momento");
        }
        return random.nextInt(81);
    }
    @Override
    public boolean verificarFuncionamento() {
        return random.nextInt(100) >= 5;
    }

    @Override
    public String getTipo() {
        return "FILA";
    }
    @Override
    public String formatar(double valor) { return String.format("%.0f pessoas na fila", valor); }
}