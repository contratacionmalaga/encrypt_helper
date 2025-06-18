package local.jarios.encryptor.api;

import local.jarios.encryptor.common.util.Constantes;
import local.jarios.encryptor.exception.EncryptorException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Implementación del servicio {@link EncryptorService} usando Jasypt para cifrado y descifrado de texto.
 * <p>
 * Permite:
 * <ul>
 *   <li>Cifrar texto plano con una clave proporcionada o por defecto</li>
 *   <li>Descifrar texto cifrado en Base64</li>
 *   <li>Configurar y recuperar una clave por defecto</li>
 * </ul>
 * <p>
 * Internamente utiliza {@link BasicTextEncryptor} de Jasypt para operaciones simétricas.
 *
 * @author Juan
 * @since 1.0.0
 */
@Getter
@Slf4j
public class EncryptorServiceImpl implements EncryptorService {

    /**
     * Clave maestra por defecto usada para cifrar/descifrar si no se proporciona una personalizada.
     * -- GETTER --
     *  Devuelve la clave por defecto actual configurada.
     */
    private String defaultKey;

    /**
     * Constructor vacío
     */
    public EncryptorServiceImpl() {
        defaultKey = Constantes.DEFAULT_SECRET_KEY;
    }


    /**
     * Establece la clave por defecto para operaciones de cifrado/descifrado.
     *
     * @param defaultKey nueva clave por defecto (no puede ser nula ni vacía)
     * @throws EncryptorException si la clave es nula o vacía
     */
    @Override
    public void setDefaultKey(String defaultKey) throws EncryptorException {
        if (defaultKey == null || defaultKey.isBlank()) {
            throw new EncryptorException("La clave por defecto no puede ser nula ni vacía.");
        }
        this.defaultKey = defaultKey;
        log.debug("Clave por defecto actualizada.");
    }

    /**
     * Cifra el texto utilizando la clave por defecto.
     *
     * @param plainText texto a cifrar
     * @return texto cifrado en Base64
     * @throws EncryptorException si el texto es nulo o ocurre un error
     */
    @Override
    public String encrypt(String plainText) {
        return encrypt(plainText, null);
    }

    /**
     * Cifra el texto utilizando una clave específica.
     *
     * @param plainText texto a cifrar
     * @param key       clave de cifrado (si es nula se usará la clave por defecto)
     * @return texto cifrado en Base64
     * @throws EncryptorException si ocurre un error en el proceso
     */
    @Override
    public String encrypt(String plainText, String key) throws EncryptorException {
        if (plainText == null) {
            throw new EncryptorException("El texto a cifrar no puede ser null.");
        }
        String finalKey = resolveKey(key);
        try {
            log.debug("Cifrando texto...");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(finalKey);
            return encryptor.encrypt(plainText);
        } catch (Exception e) {
            log.error("Error durante el cifrado", e);
            throw new EncryptorException("Error cifrando el texto", e);
        }
    }

    /**
     * Descifra el texto cifrado usando la clave por defecto.
     *
     * @param encryptedText texto cifrado en Base64
     * @return texto plano descifrado
     * @throws EncryptorException si ocurre un error o el texto es nulo
     */
    @Override
    public String decrypt(String encryptedText) {

        return decrypt(encryptedText, null);
    }

    /**
     * Descifra el texto cifrado usando una clave específica.
     *
     * @param encryptedText texto cifrado en Base64
     * @param key           clave para descifrado (si es nula se usará la clave por defecto)
     * @return texto plano descifrado
     * @throws EncryptorException si ocurre un error en el proceso
     */
    @Override
    public String decrypt(String encryptedText, String key) throws EncryptorException {
        if (encryptedText == null) {
            throw new EncryptorException("El texto a descifrar no puede ser null.");
        }
        String finalKey = resolveKey(key);
        try {
            log.debug("Descifrando texto...");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(finalKey);
            return encryptor.decrypt(encryptedText);
        } catch (Exception e) {
            log.error("Error durante el descifrado", e);
            throw new EncryptorException("Error descifrando el texto", e);
        }
    }

    /**
     * Determina la clave a utilizar en operaciones de cifrado/descifrado.
     *
     * @param providedKey clave proporcionada
     * @return clave efectiva (personalizada o por defecto)
     */
    private String resolveKey(String providedKey) {
        return (providedKey == null || providedKey.isBlank()) ? defaultKey : providedKey;
    }
}
