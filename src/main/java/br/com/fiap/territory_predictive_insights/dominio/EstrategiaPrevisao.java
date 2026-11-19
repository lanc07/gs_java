package br.com.fiap.territory_predictive_insights.dominio;

import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;

import java.util.List;

public abstract class EstrategiaPrevisao {
    private final String nome;

    protected  EstrategiaPrevisao(String nome) { this.nome = nome; }

    public String getNome() {
        return nome;
    }
    public abstract double prever(List<Medicao> historico);

    protected void validarHistorico(List<Medicao> historico, int minimo) {
        if (historico == null || historico.size() < minimo){
            throw new DadosInvalidosException("Historico insuficiente: " +
                    "precisa de "+ minimo +" leituras e tem "+(historico == null ? 0 : historico.size()));
        }
    }
}

