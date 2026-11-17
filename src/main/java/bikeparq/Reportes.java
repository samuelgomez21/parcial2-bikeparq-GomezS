package bikeparq;

import java.util.List;

public final class Reportes {

    private Reportes() {
    }

    public static Resumen resumenDelDia(List<Registro> registros) {
        int total = 0;
        int carros = 0;
        int motos = 0;

        for (Registro registro : registros) {
            if ("carro".equals(registro.tipo())) {
                carros++;
                total += registro.valor();
            } else if ("moto".equals(registro.tipo())) {
                motos++;
                total += registro.valor();
            }
        }
        return new Resumen(total, carros, motos);
    }

    public static int mayorCobro(List<Registro> registros) {
        int mayorValor = 0;

        for (Registro registro : registros) {
            if (registro.valor() > mayorValor) {
                mayorValor = registro.valor();
            }
        }
        return mayorValor;
    }
}
