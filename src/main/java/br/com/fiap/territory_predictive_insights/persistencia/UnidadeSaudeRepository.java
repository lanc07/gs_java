package br.com.fiap.territory_predictive_insights.persistencia;

import br.com.fiap.territory_predictive_insights.dominio.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {
    boolean existsByNome(String nome);
}