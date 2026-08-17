package local.jarios.encrypt.exception;

/** Excepcion personalizada que representa errores durante el cifrado o descifrado. */
public class EncryptorException extends RuntimeException {

  /**
   * Constructor con mensaje de error.
   *
   * @param message mensaje descriptivo del error
   */
  public EncryptorException(String message) {
    super(message);
  }

  /**
   * Constructor con mensaje y causa del error.
   *
   * @param message mensaje descriptivo
   * @param cause excepcion que causo el error
   */
  public EncryptorException(String message, Throwable cause) {
    super(message, cause);
  }
}
