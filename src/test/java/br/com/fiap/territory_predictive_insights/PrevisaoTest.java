package br.com.fiap.territory_predictive_insights;

import br.com.fiap.territory_predictive_insights.dominio.Medicao;
import br.com.fiap.territory_predictive_insights.dominio.PrevisaoComparacao;
import br.com.fiap.territory_predictive_insights.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoTest {
    private List<Medicao> historico(double... valores) {
        List<Medicao> lista = new ArrayList<>();
        for (double v : valores) lista.add(new Medicao("FILA", v, "UBS Centro"));
        return lista;
    }

    @Test
    void mediaMovelDeveCalcularMediaDasUltimasLeituras() {
        List<Medicao> h = historico(10, 20, 30, 40);                 // Arrange
        double previsto = new PrevisaoMediaMovel(3).prever(h);       // Act
        assertEquals(30.0, previsto, 0.001);                         // Assert (20+30+40)/3
    }

    @Test
    void mediaMovelDeveFalharComHistoricoCurto() {
        assertThrows(DadosInvalidosException.class,
                () -> new PrevisaoMediaMovel(5).prever(historico(10, 20)));
    }

    @Test
    void comparacaoDeveProjetarTendenciaDeAlta() {
        List<Medicao> h = historico(10, 10, 20, 20);                 // anterior 10, recente 20
        double previsto = new PrevisaoComparacao(2).prever(h);
        assertEquals(30.0, previsto, 0.001);                         // 20 + (20 - 10)
    }
}