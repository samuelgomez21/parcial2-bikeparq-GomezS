package bikeparq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RecargoTest {

    @Test
    void sumaRecargoEnHorarioNocturno() {
        assertEquals(5000, Recargo.aplicarRecargoNocturno(3000, 22));
    }
}
