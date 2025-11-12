package iesb.bolsa.util;

public final class TempoExecucao {

    private static long inicio;
    private static long fim;

    // Construtor privado para evitar instanciamento
    private TempoExecucao() {}

    public static void iniciar() {
        inicio = System.currentTimeMillis();
    }

    public static void finalizar() {
        fim = System.currentTimeMillis();
    }

    public static long getDuracaoMs() {
        return fim - inicio;
    }

    public static String getTempoFormatado() {
        long duracaoMs = getDuracaoMs();

        long horas = duracaoMs / (1000 * 60 * 60);
        long minutos = (duracaoMs / (1000 * 60)) % 60;
        long segundos = (duracaoMs / 1000) % 60;

        if (horas > 0) {
            return String.format("%02dh %02dm %02ds", horas, minutos, segundos);
        } else if (minutos > 0) {
            return String.format("%02dm %02ds", minutos, segundos);
        } else {
            return String.format("%02ds", segundos);
        }
    }
}

