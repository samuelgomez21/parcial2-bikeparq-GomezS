package bikeparq;

public final class Recargo {

    private Recargo() {
    }

    public static int aplicarRecargoNocturno(int tarifa, int horaEntrada) {
        if (horaEntrada < 0 || horaEntrada > 23) {
            throw new IllegalArgumentException("La hora de entrada debe estar entre 0 y 23");
        }
        if (tarifa == 0) {
            return 0;
        }
        if (horaEntrada >= 6 && horaEntrada <= 19) {
            return tarifa;
        }
        return tarifa + 2000;
    }
}
