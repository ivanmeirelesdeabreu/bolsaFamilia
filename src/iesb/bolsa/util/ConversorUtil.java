package iesb.bolsa.util;

public final class ConversorUtil {

    private ConversorUtil() {
    }

    public static double conversaoValor(String s) {
        return Double.parseDouble(
            s.replaceAll("\"","")
                .replace(",", "."));
    }
}
