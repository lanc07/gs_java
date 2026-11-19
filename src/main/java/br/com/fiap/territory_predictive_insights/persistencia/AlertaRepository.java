package br.com.fiap.territory_predictive_insights.persistencia;

import br.com.fiap.territory_predictive_insights.dominio.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
