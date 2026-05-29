package co.edu.uptc.exception;

/**
 * Indica que no existe un inversionista con el identificador consultado.
 */
public class InvestorNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message detalle del error
     */
    public InvestorNotFoundException(String message) {
        super(message);
    }
}
