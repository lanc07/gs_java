package br.com.fiap.territory_predictive_insights.dominio;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipoSensor;
    private String unidade;
    private double valorPrevisto;
    private double limite;
    @Enumerated(EnumType.STRING)
    private NivelAlerta nivel;
    private String mensagem;
    private LocalDateTime dataHora;

    protected Alerta(){ }

    public Alerta(String tipoSensor, String unidade, double valorPrevisto,
                  double limite, NivelAlerta nivel, String estrategia) {
        this.tipoSensor = tipoSensor;
        this.unidade = unidade;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.nivel = nivel;
        this.mensagem = unidade + ": " + tipoSensor + " deve chegar a "
                + String.format("%.1f", valorPrevisto) + " (limite " + String.format("%.1f", limite)
                + "), segundo " + estrategia + ". " + nivel.getOrientacao() + ".";
        this.dataHora = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getTipoSensor() { return tipoSensor; }
    public NivelAlerta getNivel() { return nivel; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataHora() { return dataHora; }
}
