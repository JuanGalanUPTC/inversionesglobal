package co.edu.uptc.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class AppConfig {

    public enum CurrencyMode {
        USD, COP
    }

    private static CurrencyMode currentCurrency = CurrencyMode.USD;

    // Ahora es variable y arranca en un precio promedio base
    private static double usdToCopRate = 4000.0;

    private static final Random random = new Random();

    public static void setCurrencyMode(CurrencyMode mode) {
        currentCurrency = mode;
    }

    public static CurrencyMode getCurrencyMode() {
        return currentCurrency;
    }

    public static double getUsdToCopRate() {
        return usdToCopRate;
    }

    /**
     * Hace fluctuar el precio del dólar frente al peso colombiano.
     * Se puede llamar en el mismo hilo que hace fluctuar los activos.
     */
    public static void simulateDollarFluctuation() {
        // El dólar en Colombia se mueve sutilmente día a día, digamos un max 0.5% por
        // ciclo
        double maxChangePercent = 0.005;
        double percentChange = (random.nextDouble() * 2 - 1) * maxChangePercent;

        usdToCopRate = usdToCopRate + (usdToCopRate * percentChange);

        // Límites realistas para que el dólar en tu app no valga $10 pesos ni $20.000
        if (usdToCopRate < 3500.0)
            usdToCopRate = 3500.0;
        if (usdToCopRate > 5000.0)
            usdToCopRate = 5000.0;

        // Redondear a 2 decimales para la TRM
        usdToCopRate = Math.round(usdToCopRate * 100.0) / 100.0;
    }

    /**
     * Transforma y formatea el dinero usando la TRM (Tasa Representativa del
     * Mercado) actual.
     */
    public static String formatMoney(double amountInUSD) {
        double convertedAmount = amountInUSD;
        NumberFormat formatter;

        if (currentCurrency == CurrencyMode.COP) {
            // Usa la tasa fluctuante del momento
            convertedAmount = amountInUSD * usdToCopRate;

            // FORMA MODERNA (Java 19+): Uso de Factory Method con formato IETF BCP 47
            formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

            // Alternativa con Builder por si prefieres separar los parámetros:
            // formatter = NumberFormat.getCurrencyInstance(new
            // Locale.Builder().setLanguage("es").setRegion("CO").build());

            formatter.setMaximumFractionDigits(0);
        } else {
            formatter = NumberFormat.getCurrencyInstance(Locale.US);
        }

        return formatter.format(convertedAmount);
    }
}