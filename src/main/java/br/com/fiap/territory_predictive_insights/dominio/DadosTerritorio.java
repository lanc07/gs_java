package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;

import java.util.HashMap;
import java.util.Map;

public class DadosTerritorio {
    private final Map<String, Double> limiares = new HashMap<>();
    private final String codigoAcesso;

    public DadosTerritorio(String codigoAcesso) {
        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            throw new DadosInvalidosException("Código de acesso nao pode ser " +
                    "vazio. Tente novamente ");
        }
        this.codigoAcesso = codigoAcesso;
        limiares.put("FILA", 30.0);
        limiares.put("TEMPO_ESPERA", 60.0);
        limiares.put("OCUPACAO", 85.0);
    }
    public double getLimiar(String tipoSensor){
        Double limiar = limiares.get(tipoSensor);
        if(limiar == null) throw new DadosInvalidosException("Sensor sem " +
                "limiar cadastrado: "+ tipoSensor);
        return limiar;
    }
    public void setLimiar(String tipoSensor, double novoLimiar, String codigo) {
        if (!codigoAcesso.equals(codigo)) throw new DadosInvalidosException(
                "Código de acesso incorreto.");
        if(!limiares.containsKey(tipoSensor))throw new DadosInvalidosException("Sensor desconhecido "+ tipoSensor);
        if (Double.isNaN(novoLimiar) || Double.isInfinite(novoLimiar) || novoLimiar <= 0) {
            throw new DadosInvalidosException("O limite precisa ser um número maior que zero: " + novoLimiar);
        }
        limiares.put(tipoSensor, novoLimiar);
    }
    public Map<String, Double> getLimiares() {return Map.copyOf(limiares);}
}
