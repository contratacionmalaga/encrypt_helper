package local.jarios.encrypt.exception;

/**
 * Excepción personalizada que representa errores durante el cifrado o descifrado.
 */
public class EncryptException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message mensaje descriptivo del error
     */
    public EncryptException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa del error.
     *
     * @param message mensaje descriptivo
     * @param cause   excepción que causó el error
     */
    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
