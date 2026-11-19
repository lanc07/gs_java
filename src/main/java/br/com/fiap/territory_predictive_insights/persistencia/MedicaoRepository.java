package br.com.fiap.territory_predictive_insights.persistencia;

import br.com.fiap.territory_predictive_insights.dominio.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {
    List<Medicao> findByTipoSensor (String tipoSensor);

}