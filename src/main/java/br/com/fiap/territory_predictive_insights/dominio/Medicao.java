package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Medicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipoSensor;
    private double valor;
    private String unidade;
    private LocalDateTime dataHora;
    protected Medicao() { }

    public Medicao(String tipoSensor, double valor, String unidade) {
        validar(tipoSensor, valor, unidade);
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.unidade = unidade;
        this.dataHora = LocalDateTime.now();
    }

    //getters
    public Long getId() { return id; }
    public String getTipoSensor() { return tipoSensor; }
    public double getValor() { return valor; }
    public String getUnidade() { return unidade; }
    public LocalDateTime getDataHora() {return dataHora; }

    // validação
    private void validar(String tipoSensor, double valor, String unidade) {
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("O tipo do sensor não pode " +
                    "ficar vazio. Tente novamente ");
        }
        if (valor < 0) {
            throw new DadosInvalidosException("Medição de " + tipoSensor + " " +
                    "não pode ser negativa: " + valor +". Tente novamente.");
        }
        if (unidade == null || unidade.isBlank()) {
            throw new DadosInvalidosException("O campo da unidade não pode " +
                    "estar vazio. Tente novamente");
        }

    }

}
