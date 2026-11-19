package br.com.fiap.territory_predictive_insights.servico;

import br.com.fiap.territory_predictive_insights.dominio.Alerta;
import br.com.fiap.territory_predictive_insights.dominio.EstrategiaPrevisao;
import br.com.fiap.territory_predictive_insights.dominio.Medicao;
import br.com.fiap.territory_predictive_insights.dominio.NivelAlerta;
import br.com.fiap.territory_predictive_insights.dominio.Sensor;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights.persistencia.AlertaRepository;
import br.com.fiap.territory_predictive_insights.persistencia.MedicaoRepository;
import br.com.fiap.territory_predictive_insights.persistencia.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final UnidadeSaudeRepository unidadeRepository;
    private final AlertaRepository alertaRepository;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository,
                                UnidadeSaudeRepository unidadeRepository,
                                AlertaRepository alertaRepository) {
        this.medicaoRepository = medicaoRepository;
        this.unidadeRepository = unidadeRepository;
        this.alertaRepository = alertaRepository;
    }

    public Medicao registrarLeitura(Sensor sensor, String unidade) {
        if (!unidadeRepository.existsByNome(unidade)) {
            throw new DadosInvalidosException("Unidade inexistente: " + unidade);
        }
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getTipo(), valor, unidade);
        return medicaoRepository.save(medicao);
    }

    public List<Medicao> listarHistorico(String tipoSensor) {
        return medicaoRepository.findByTipoSensor(tipoSensor);
    }

    public double prever(String tipoSensor, EstrategiaPrevisao estrategia) {
        List<Medicao> historico = medicaoRepository.findByTipoSensor(tipoSensor);
        return estrategia.prever(historico);
    }

    public Optional<Alerta> emitirAlertaSeNecessario(String tipoSensor, String unidade,
                                                     String estrategia, double previsto) {
        double limite = ConfiguracaoSistema.getInstance().getDados().getLimiar(tipoSensor);
        NivelAlerta nivel = classificar(previsto, limite);
        if (nivel == null) {
            return Optional.empty();
        }
        Alerta alerta = new Alerta(tipoSensor, unidade, previsto, limite, nivel, estrategia);
        alertaRepository.save(alerta);
        return Optional.of(alerta);
    }

    public List<Alerta> listarAlertas() {
        return alertaRepository.findAll();
    }

    private NivelAlerta classificar(double previsto, double limite) {
        if (previsto >= limite * 1.2) return NivelAlerta.CRITICO;
        if (previsto >= limite)       return NivelAlerta.ALERTA;
        if (previsto >= limite * 0.8) return NivelAlerta.ATENCAO;
        return null;
    }
}