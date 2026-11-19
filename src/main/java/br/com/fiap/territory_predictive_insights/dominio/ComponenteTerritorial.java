package br.com.fiap.territory_predictive_insights.dominio;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;



@MappedSuperclass
public abstract class ComponenteTerritorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String regiao;
    private String status;

    protected ComponenteTerritorial() { }

    protected ComponenteTerritorial(String nome, String regiao) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome da unidade não pode ficar vazio.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região não pode ficar vazia.");
        }

        this.nome = nome;
        this.regiao = regiao;
        this.status = "ATIVO";
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getRegiao() { return regiao; }
    public String getStatus() { return status; }

    public abstract String verificarStatus();
}