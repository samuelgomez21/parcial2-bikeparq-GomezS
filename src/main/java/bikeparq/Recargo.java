package bikeparq;

public final class Recargo {

    private Recargo() {
    }

    public static int aplicarRecargoNocturno(int tarifa, int horaEntrada) {
        if (tarifa == 0) {
            return 0;
        }
        if (horaEntrada >= 6 && horaEntrada <= 19) {
            return tarifa;
        }
        return tarifa + 2000;
    }
}
