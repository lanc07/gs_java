package br.com.fiap.territory_predictive_insights.dominio;

public interface Sensor {
    String formatar(double valor);
    double lerValor();
    boolean verificarFuncionamento();
    String getTipo();
}
