package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.FonteIndisponivelException;

import java.util.Random;

public class SensorTempoEspera implements Sensor {
    private final Random random = new Random();

    @Override
    public double lerValor(){
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de tempo de espera " +
                    "indiponivel");
        }
        return 5 + random.nextInt(176);
    }
    @Override
    public boolean verificarFuncionamento() { return random.nextInt(100) >= 5; }
    @Override
    public String getTipo() { return "TEMPO_ESPERA";}
    @Override
    public String formatar(double valor) { return String.format("%.0f min de espera", valor); }
}
