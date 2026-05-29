package co.edu.uptc.exception;

/**
 * Indica que no existe un activo con el identificador consultado.
 */
public class AssetNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message detalle del error
     */
    public AssetNotFoundException(String message) {
        super(message);
    }
}
