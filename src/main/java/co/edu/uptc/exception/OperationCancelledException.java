package co.edu.uptc.exception;

/**
 * Indica que el usuario canceló una operación interactiva (por ejemplo ingresando 'X').
 */
public class OperationCancelledException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message detalle de cancelación
     */
    public OperationCancelledException(String message) {
        super(message);
    }
}
