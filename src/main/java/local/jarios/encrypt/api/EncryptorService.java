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
     * Establece la clave por defecto que se usará si no se proporciona una explícita.
     *
     * @param encryptKey clave maestra por defecto (no puede ser nula o vacía)
     * @throws EncryptorException si la clave es nula o vacía
     */
    void setEncryptKey(String encryptKey) throws EncryptorException;

    /**
     * Devuelve la clave de encriptación
     *
     * @return la clave por defecto que se usará si no se proporciona una explícita.
     */
    String getEncryptKey();

    /**
     * Cifra el texto usando la clave por defecto.
     *
     * @param plainText texto plano a cifrar
     * @return texto cifrado en Base64
     * @throws EncryptorException si el texto es inválido o no hay clave configurada
     */
    String encryptDefaultKey(String plainText) throws EncryptorException;

    /**
     * Cifra el texto usando una clave personalizada.
     *
     * @param plainText texto plano a cifrar
     * @param key       clave personalizada para el cifrado
     * @return texto cifrado en Base64
     */
    String encrypt(String plainText, String key) throws EncryptorException;

    /**
     * Descifra el texto cifrado usando la clave por defecto.
     *
     * @param encryptedText texto cifrado en Base64
     * @return texto descifrado
     * @throws EncryptorException si el texto es inválido o no hay clave configurada
     */
    String decryptDefaultKey(String encryptedText) throws EncryptorException;

    /**
     * Descifra el texto cifrado usando una clave personalizada.
     *
     * @param encryptedText texto cifrado en Base64
     * @param key           clave personalizada para el descifrado
     * @return texto descifrado
     */
    String decrypt(String encryptedText, String key) throws EncryptorException;
}
