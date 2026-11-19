package br.com.fiap.territory_predictive_insights.dominio;
import jakarta.persistence.Entity;



@Entity
public class UnidadeSaude extends ComponenteTerritorial {

    protected UnidadeSaude() { }

    public UnidadeSaude(String nome, String regiao) {
        super(nome, regiao);
    }

    @Override
    public String verificarStatus() {
        return "Unidade " + getNome() + " (" + getRegiao() + "): " + getStatus();
    }
}