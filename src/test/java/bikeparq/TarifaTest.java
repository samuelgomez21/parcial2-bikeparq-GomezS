package bikeparq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class TarifaTest {

    @Test
    void cobraTresMilPesosPorUnCarroDuranteSesentaMinutos() {
        assertEquals(3000, Tarifa.calcularTarifa(60, "carro"));
    }

    @Test
    void noCobraElUltimoMinutoDeGraciaParaUnaMoto() {
        assertEquals(0, Tarifa.calcularTarifa(10, "moto"));
    }

    @Test
    void rechazaUnTipoDeVehiculoNoValido() {
        assertThrows(IllegalArgumentException.class, () -> Tarifa.calcularTarifa(60, "bicicleta"));
    }

}
