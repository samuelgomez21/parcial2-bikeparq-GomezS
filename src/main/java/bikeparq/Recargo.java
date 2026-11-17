package bikeparq;

public final class Recargo {

    private static final int HORA_MINIMA = 0;
    private static final int HORA_MAXIMA = 23;
    private static final int INICIO_DIA = 6;
    private static final int INICIO_NOCHE = 20;
    private static final int VALOR_RECARGO_NOCTURNO = 2_000;

    private Recargo() {
    }

    public static int aplicarRecargoNocturno(int tarifa, int horaEntrada) {
        if (!esHoraValida(horaEntrada)) {
            throw new IllegalArgumentException("La hora de entrada debe estar entre 0 y 23");
        }
        if (tarifa == 0) {
            return 0;
        }
        if (!esHorarioNocturno(horaEntrada)) {
            return tarifa;
        }
        return tarifa + VALOR_RECARGO_NOCTURNO;
    }

    private static boolean esHoraValida(int horaEntrada) {
        return horaEntrada >= HORA_MINIMA && horaEntrada <= HORA_MAXIMA;
    }

    private static boolean esHorarioNocturno(int horaEntrada) {
        return horaEntrada < INICIO_DIA || horaEntrada >= INICIO_NOCHE;
    }
}
