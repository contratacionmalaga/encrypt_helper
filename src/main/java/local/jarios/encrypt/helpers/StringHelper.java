package local.jarios.encrypt.helpers;

/**
 * Utilidades de validacion de cadenas.
 */
public final class StringHelper {

  private StringHelper() {
  }

  /**
   * Indica si una cadena es nula.
   *
   * @param value cadena a validar
   * @return {@code true} si la cadena es nula
   */
  public static boolean isNull(String value) {
    return value == null;
  }

  /**
   * Indica si una cadena es nula o blanca.
   *
   * @param value cadena a validar
   * @return {@code true} si la cadena es nula o blanca
   */
  public static boolean isNullOrBlank(String value) {
    return value == null || value.isBlank();
  }

  /**
   * Indica si una cadena es nula o blanca.
   *
   * @param value cadena a validar
   * @return {@code true} si la cadena es nula o blanca
   * @deprecated usar {@link #isNullOrBlank(String)} para claves y textos cifrados, o
   *     {@link #isNull(String)} para payloads que puedan ser vacios.
   */
  @Deprecated(since = "7.0.0", forRemoval = false)
  public static boolean isInvalidString(String value) {
    return isNullOrBlank(value);
  }
}
