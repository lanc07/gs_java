package br.com.fiap.territory_predictive_insights;

import br.com.fiap.territory_predictive_insights.dominio.DadosTerritorio;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DadosTerritorioTest {
    @Test
    void deveAlterarLimiarComCodigoCorreto() {
        DadosTerritorio dados = new DadosTerritorio("abc");
        dados.setLimiar("FILA", 45, "abc");
        assertEquals(45.0, dados.getLimiar("FILA"), 0.001);
    }

    @Test
    void deveRecusarCodigoErrado() {
        DadosTerritorio dados = new DadosTerritorio("abc");
        assertThrows(DadosInvalidosException.class, () -> dados.setLimiar("FILA", 45, "errado"));
    }

    @Test
    void deveRecusarLimiarInvalido() {
        DadosTerritorio dados = new DadosTerritorio("abc");
        assertThrows(DadosInvalidosException.class, () -> dados.setLimiar("FILA", Double.NaN, "abc"));
        assertThrows(DadosInvalidosException.class, () -> dados.setLimiar("FILA", -5, "abc"));
    }
}
