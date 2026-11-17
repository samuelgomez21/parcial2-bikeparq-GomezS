package bikeparq;

import java.util.Map;

/** Calcula el valor de la permanencia de un vehiculo en BikeParq. */
public final class Tarifa {

    private static final Map<String, Integer> PRECIO_POR_MINUTO = Map.of("carro", 60, "moto", 30);
    private static final Map<String, Integer> TOPE_DIARIO = Map.of("carro", 25_000, "moto", 12_000);
    private static final int MINUTOS_DE_GRACIA = 10;
    private static final int MINUTOS_MAXIMOS = 1_440;

    private Tarifa() {
    }

    /** Devuelve la tarifa para los minutos de permanencia y el tipo de vehiculo indicados. */
    public static int calcularTarifa(int minutos, String tipo) {
        if (!PRECIO_POR_MINUTO.containsKey(tipo)) {
            throw new IllegalArgumentException("Tipo de vehiculo no valido: " + tipo);
        }
        if (minutos < 0 || minutos > MINUTOS_MAXIMOS) {
            throw new IllegalArgumentException("Los minutos deben estar entre 0 y 1440");
        }
        if (minutos <= MINUTOS_DE_GRACIA) {
            return 0;
        }
        int valor = (minutos - MINUTOS_DE_GRACIA) * PRECIO_POR_MINUTO.get(tipo);
        return Math.min(valor, TOPE_DIARIO.get(tipo));
    }
}
