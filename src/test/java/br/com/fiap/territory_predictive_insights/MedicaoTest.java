package br.com.fiap.territory_predictive_insights;

import br.com.fiap.territory_predictive_insights.dominio.Medicao;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MedicaoTest {
    @Test
    void deveLancarExcecaoQuandoValorNegativo() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("FILA", -5, "UBS Centro"));
    }

    @Test
    void deveGuardarDadosQuandoValido() {
        Medicao m = new Medicao("FILA", 25, "UBS Centro");          // Arrange + Act
        assertEquals(25, m.getValor());                              // Assert
        assertEquals("FILA", m.getTipoSensor());
    }
}
