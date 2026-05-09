package local.jarios.encrypt.api;

import local.jarios.encrypt.exception.EncryptorException;
import local.jarios.encrypt.helpers.StringHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Implementación de {@link EncryptorService} usando Jasypt para cifrado y descifrado de texto.
 * <p>
 * Permite:
 * <ul>
 *   <li>Cifrar texto plano con una clave proporcionada o por defecto</li>
 *   <li>Descifrar texto cifrado en Base64</li>
 *   <li>Configurar y recuperar una clave por defecto proporcionada externamente</li>
 * </ul>
 * <p>
 * Internamente utiliza {@link BasicTextEncryptor} de Jasypt para operaciones simétricas.
 * Autor: Juan
 * @since 1.0.0
 */
@Getter
@Slf4j
public final class EncryptorServiceImpl implements EncryptorService {

    /** Clave por defecto usada para cifrado/descifrado. */
    private String encryptKey;

    /** Constructor por defecto. La clave debe configurarse explicitamente antes de usar los metodos por defecto. */
    public EncryptorServiceImpl() {
        this.encryptKey = null;
    }

    /**
     * Constructor que configura la clave de cifrado de forma explicita.
     *
     * @param encryptKey clave de cifrado
     * @throws EncryptorException si la clave es nula o vacia
     */
    public EncryptorServiceImpl(String encryptKey) throws EncryptorException {
        setEncryptKey(encryptKey);
    }

    /**
     * Establece la clave por defecto para cifrado/descifrado.
     *
     * @param encryptKey nueva clave
     * @throws EncryptorException si la clave es nula o vacía
     */
    @Override
    public void setEncryptKey(String encryptKey) throws EncryptorException {
        if (StringHelper.isInvalidString(encryptKey)) {
            String msg = "Clave nula o vacía no permitida.";
            log.error(msg);
            throw new EncryptorException(msg);
        }
        this.encryptKey = encryptKey;
        log.debug("Definida la clave de encriptación correctamente.");
    }

    /**
     * Cifra texto usando la clave por defecto.
     *
     * @param plainText texto a cifrar
     * @return texto cifrado en Base64
     * @throws EncryptorException si el texto es nulo o vacío
     */
    @Override
    public String encryptDefaultKey(String plainText) throws EncryptorException {
        ensureEncryptKeyConfigured();
        String encryptedText = encrypt(plainText, this.encryptKey);
        log.debug("Texto cifrado con clave por defecto correctamente.");
        return encryptedText;
    }

    /**
     * Cifra texto usando una clave específica.
     *
     * @param plainText texto a cifrar
     * @param key clave de cifrado (usa por defecto si es nula)
     * @return texto cifrado en Base64
     * @throws EncryptorException si el texto o la clave son inválidos
     */
    @Override
    public String encrypt(String plainText, String key) throws EncryptorException {
        if (StringHelper.isInvalidString(plainText)) {
            String msg = "Texto a cifrar nulo o vacío.";
            log.error(msg);
            throw new EncryptorException(msg);
        }
        if (StringHelper.isInvalidString(key)) {
            String msg = "Clave de cifrado nula o vacía.";
            log.error(msg);
            throw new EncryptorException(msg);
        }

        try {
            log.info("Inicio del proceso de encriptación.");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(key);

            String encryptedText = encryptor.encrypt(plainText);
            log.info("Texto cifrado correctamente.");
            return encryptedText;

        } catch (Exception ex) {
            String msg = "Error inesperado durante el proceso de cifrado.";
            log.error(msg);
            log.debug(msg, ex);
            throw new EncryptorException(msg, ex);
        }
    }

    /**
     * Descifra texto cifrado usando la clave por defecto.
     *
     * @param encryptedText texto cifrado en Base64
     * @return texto plano descifrado
     * @throws EncryptorException si el texto es nulo o ocurre un error
     */
    @Override
    public String decryptDefaultKey(String encryptedText) throws EncryptorException {
        ensureEncryptKeyConfigured();
        String plainText = decrypt(encryptedText, this.encryptKey);
        log.debug("Texto descifrado correctamente");
        return plainText;
    }

    /**
     * Descifra texto cifrado usando una clave específica.
     *
     * @param encryptedText texto cifrado en Base64
     * @param key clave de descifrado (usa por defecto si es nula)
     * @return texto plano descifrado
     * @throws EncryptorException si el texto o la clave son inválidos
     */
    @Override
    public String decrypt(String encryptedText, String key) throws EncryptorException {
        if (StringHelper.isInvalidString(encryptedText)) {
            String msg = "Texto a descifrar nulo o vacío.";
            log.error(msg);
            throw new EncryptorException(msg);
        }
        if (StringHelper.isInvalidString(key)) {
            String msg = "Clave de descifrado nula o vacía.";
            log.error(msg);
            throw new EncryptorException(msg);
        }

        try {
            log.info("Inicio del proceso de descifrado.");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(key);

            String plainText = encryptor.decrypt(encryptedText);
            log.info("Texto descifrado correctamente.");
            return plainText;

        } catch (Exception ex) {
            String msg = "Error inesperado durante el proceso de descifrado.";
            log.error(msg);
            log.debug(msg, ex);
            throw new EncryptorException(msg, ex);
        }
    }

    private void ensureEncryptKeyConfigured() throws EncryptorException {
        if (StringHelper.isInvalidString(this.encryptKey)) {
            String msg = "Clave por defecto no configurada.";
            log.error(msg);
            throw new EncryptorException(msg);
        }
    }
}
