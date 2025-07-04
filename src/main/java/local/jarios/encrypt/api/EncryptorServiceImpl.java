package local.jarios.encrypt.api;

import local.jarios.encrypt.common.util.Constantes;
import local.jarios.encrypt.exception.EncryptorException;
import local.jarios.encrypt.helpers.StringHelper;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class EncryptorServiceImpl implements EncryptorService {

    /**
     * Instancia única (singleton) del gestor de propiedades.
     * Inicialización temprana y thread-safe mediante static final.
     */
    private static final Logger LOGGER = LogManager.getLogger("local.jarios.encrypt");

    /**
     * Clave maestra por defecto usada para cifrar/descifrar si no se proporciona una personalizada.
     * -- GETTER --
     *  Devuelve la clave por defecto actual configurada.
     */
    private String encryptKey;

    /**
     * Constructor vacío
     */
    public EncryptorServiceImpl() {
        encryptKey = Constantes.DEFAULT_SECRET_KEY;
    }


    /**
     * Establece la clave por defecto para operaciones de cifrado/descifrado.
     *
     * @param encryptKey nueva clave por defecto (no puede ser nula ni vacía)
     * @throws EncryptorException si la clave es nula o vacía
     */
    public void setEncryptKey(String encryptKey) throws EncryptorException {

        if (StringHelper.isInvalidString(encryptKey)) {
            String msg = "[setDefaultKey] - El valor de la clave no puede ser NULL o BLANK.";
            LOGGER.debug(msg);
            throw new EncryptorException(msg);
        }
        this.encryptKey = encryptKey;
        LOGGER.debug("[setDefaultKey] - Clave por defecto actualizada.");
    }

    /**
     * Cifra el texto utilizando la clave establecida. En caso de haber establecido clave se encritpa con la clave
     *      por defecto establecida en la constante DEFAULT_SECRET_KEY
     *
     * @param plainText texto a cifrar
     * @return texto cifrado en Base64
     * @throws EncryptorException si el texto es nulo o ocurre un error
     */
    public String encryptDefaultKey(String plainText) {


        String encryptedText = encrypt(plainText, encryptKey);
        LOGGER.debug(
                "[encryptDefaultKey] - Encriptando el texto: '{}' utilizando como key: {}.",
                encryptedText,
                encryptKey);
        return encryptedText;
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

        if (StringHelper.isInvalidString(plainText)) {
            String msg = "[encrypt] - El texto a cifrar es NULL | BLANK.";
            LOGGER.error(msg);
            throw new EncryptorException(msg);
        }

        if (StringHelper.isInvalidString(key)) {
            String msg = "[encrypt] - La clave para cifrar es NULL | BLANK.";
            LOGGER.error(msg);
            throw new EncryptorException(msg);
        }

        try {

            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            LOGGER.debug("[encrypt] - Creado el objeto BasicTextEncryptor correctamente.");
            encryptor.setPassword(key);
            LOGGER.debug("[encrypt] - Establecida la clave de cifrado correctamente.");
            String encryptText = encryptor.encrypt(plainText);
            LOGGER.debug("[encrypt] - Texto cifrado correctamente. Texto: '{}'", encryptText);
            return encryptText;

        } catch (Exception ex) {

            String msg = String.format("[encrypt] - Error durante el cifrado. Error: %s", ex.getMessage());
            LOGGER.error(msg, ex);
            throw new EncryptorException(msg, ex);

        }
    }

    /**
     * Descifra el texto cifrado usando la clave por defecto.
     *
     * @param encryptedText texto cifrado en Base64
     * @return texto plano descifrado
     * @throws EncryptorException si ocurre un error o el texto es nulo
     */
    public String decryptDefaultKey(String encryptedText) {

        if (StringHelper.isInvalidString(encryptedText)) {
            String msg = "[decryptDefaultKey] - El texto a descifrar es NULL | BLANK.";
            LOGGER.error(msg);
            throw new EncryptorException(msg);
        }

        String plainText =  decrypt(encryptedText, encryptKey);
        LOGGER.error("[decryptDefaultKey] - Texto descifrado correctamente. Texto: '{}'.", plainText);
        return plainText;
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

        if (StringHelper.isInvalidString(encryptedText)) {
            String msg = "[decrypt] - El texto a descifrar es NULL | BLANK.";
            LOGGER.error(msg);
            throw new EncryptorException(msg);
        }

        if (StringHelper.isInvalidString(key)) {
            String msg = "[decrypt] - La clave para cifrar es NULL | BLANK.";
            LOGGER.error(msg);
            throw new EncryptorException(msg);
        }

        try {

            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            LOGGER.debug("[decrypt] - Creado el objeto BasicTextEncryptor correctamente.");
            encryptor.setPassword(key);
            LOGGER.debug("[decrypt] - Establecida la clave de cifrado correctamente.");
            String plainText = encryptor.decrypt(encryptedText);
            LOGGER.debug("[decrypt] - Texto descifrado correctamente. Texto: '{}'", plainText);
            return plainText;

        } catch (Exception ex) {

            String msg = String.format("[decrypt] - Error durante el descifrado. Error: %s", ex.getMessage());
            LOGGER.error(msg, ex);
            throw new EncryptorException(msg, ex);

        }
    }
}
