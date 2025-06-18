package local.jarios.encryptor.api;

import local.jarios.encryptor.exception.EncryptorException;

/**
 * Servicio de cifrado y descifrado de texto.
 * Permite operar con una clave maestra definida por defecto o pasada en cada llamada.
 *
 * @author Juan
 * @since 1.0.0
 */
public interface EncryptorService {

    /**
     * Establece la clave por defecto que se usará si no se proporciona una explícita.
     *
     * @param defaultKey clave maestra por defecto (no puede ser nula o vacía)
     */
    void setDefaultKey(String defaultKey);

    /**
     * @return la clave por defecto que se usará si no se proporciona una explícita.
     */
    String getDefaultKey();

    /**
     * Cifra el texto usando la clave por defecto.
     *
     * @param plainText texto plano a cifrar
     * @return texto cifrado en Base64
     */
    String encrypt(String plainText);

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
     */
    String decrypt(String encryptedText);

    /**
     * Descifra el texto cifrado usando una clave personalizada.
     *
     * @param encryptedText texto cifrado en Base64
     * @param key           clave personalizada para el descifrado
     * @return texto descifrado
     */
    String decrypt(String encryptedText, String key) throws EncryptorException;
}
