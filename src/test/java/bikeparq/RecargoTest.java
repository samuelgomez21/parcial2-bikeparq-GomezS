package bikeparq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RecargoTest {

    @Test
    void sumaRecargoEnHorarioNocturno() {
        assertEquals(5000, Recargo.aplicarRecargoNocturno(3000, 22));
    }

    @Test
    void noSumaRecargoEnHorarioDiurnoYEnSusLimites() {
        assertEquals(3000, Recargo.aplicarRecargoNocturno(3000, 10));
        assertEquals(3000, Recargo.aplicarRecargoNocturno(3000, 19));
        assertEquals(3000, Recargo.aplicarRecargoNocturno(3000, 6));
    }

    @Test
    void noSumaRecargoCuandoLaTarifaEsCero() {
        assertEquals(0, Recargo.aplicarRecargoNocturno(0, 23));
    }

    @Test
    void rechazaUnaHoraFueraDelRangoValido() {
        assertThrows(IllegalArgumentException.class, () -> Recargo.aplicarRecargoNocturno(3000, 24));
    }
}
