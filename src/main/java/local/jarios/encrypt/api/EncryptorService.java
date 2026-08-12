package local.jarios.encrypt.api;

import local.jarios.encrypt.exception.EncryptorException;

/**
 * Servicio de cifrado y descifrado de texto.
 * Permite operar con una clave configurada externamente o pasada en cada llamada.
 *
 * @author Juan
 * @since 1.0.0
 */
public interface EncryptorService {

  /**
   * Establece la clave por defecto que se usara si no se proporciona una explicita.
   *
   * @param encryptKey clave maestra por defecto, no puede ser nula ni blanca
   * @throws EncryptorException si la clave es nula o blanca
   */
  void setEncryptKey(String encryptKey) throws EncryptorException;

  /**
   * Indica si hay una clave por defecto configurada.
   *
   * @return {@code true} si existe clave configurada
   */
  boolean hasEncryptKeyConfigured();

  /**
   * Cifra el texto usando la clave por defecto.
   *
   * @param plainText texto plano a cifrar
   * @return texto cifrado con formato versionado
   * @throws EncryptorException si el texto es nulo o no hay clave configurada
   */
  String encryptDefaultKey(String plainText) throws EncryptorException;

  /**
   * Cifra el texto usando una clave personalizada.
   *
   * @param plainText texto plano a cifrar
   * @param key clave personalizada para el cifrado
   * @return texto cifrado con formato versionado
   * @throws EncryptorException si el texto es nulo o la clave es invalida
   */
  String encrypt(String plainText, String key) throws EncryptorException;

  /**
   * Descifra el texto cifrado usando la clave por defecto.
   *
   * @param encryptedText texto cifrado
   * @return texto descifrado
   * @throws EncryptorException si el texto es invalido o no hay clave configurada
   */
  String decryptDefaultKey(String encryptedText) throws EncryptorException;

  /**
   * Descifra el texto cifrado usando una clave personalizada.
   *
   * @param encryptedText texto cifrado
   * @param key clave personalizada para el descifrado
   * @return texto descifrado
   * @throws EncryptorException si el texto o la clave son invalidos
   */
  String decrypt(String encryptedText, String key) throws EncryptorException;
}
