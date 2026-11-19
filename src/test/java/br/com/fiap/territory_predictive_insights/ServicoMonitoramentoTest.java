package br.com.fiap.territory_predictive_insights;

import br.com.fiap.territory_predictive_insights.dominio.Alerta;
import br.com.fiap.territory_predictive_insights.dominio.NivelAlerta;
import br.com.fiap.territory_predictive_insights.dominio.SensorFila;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights.persistencia.AlertaRepository;
import br.com.fiap.territory_predictive_insights.persistencia.MedicaoRepository;
import br.com.fiap.territory_predictive_insights.persistencia.UnidadeSaudeRepository;
import br.com.fiap.territory_predictive_insights.servico.ServicoMonitoramento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {
    @Mock
    MedicaoRepository medicaoRepository;
    @Mock
    UnidadeSaudeRepository unidadeRepository;
    @Mock
    AlertaRepository alertaRepository;
    @InjectMocks
    ServicoMonitoramento servico;

    @Test
    void deveEmitirAlertaCriticoQuandoPrevistoMuitoAcimaDoLimite() {
        // limite padrão de FILA = 30; 40 passa de 36 (120%)
        Optional<Alerta> alerta = servico.emitirAlertaSeNecessario("FILA", "UBS Centro", "teste", 40);
        assertTrue(alerta.isPresent());
        assertEquals(NivelAlerta.CRITICO, alerta.get().getNivel());
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void naoDeveEmitirAlertaQuandoPrevistoBaixo() {
        Optional<Alerta> alerta = servico.emitirAlertaSeNecessario("FILA", "UBS Centro", "teste", 10);
        assertTrue(alerta.isEmpty());
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void naoDeveGravarMedicaoQuandoUnidadeNaoExiste() {
        when(unidadeRepository.existsByNome("UBS X")).thenReturn(false);
        assertThrows(DadosInvalidosException.class,
                () -> servico.registrarLeitura(new SensorFila(), "UBS X"));
        verify(medicaoRepository, never()).save(any());
    }
}