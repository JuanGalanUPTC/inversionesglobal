package co.edu.uptc.exception;

/**
 * Indica que el inversionista no dispone de capital suficiente para la operación solicitada.
 */
public class InsufficientCapitalException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message detalle del error
     */
    public InsufficientCapitalException(String message) {
        super(message);
    }
}
