package co.edu.uptc.exception;

/**
 * Indica que el perfil de riesgo del inversionista no permite invertir en el tipo de activo indicado.
 */
public class IncompatibleRiskProfileException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message detalle del error
     */
    public IncompatibleRiskProfileException(String message) {
        super(message);
    }
}
