package local.jarios.encryptor.config;

import local.jarios.encryptor.exception.EncryptorException;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Clase utilitaria para cifrado y descifrado de texto usando Jasypt con una clave maestra.
 *
 * Esta clase proporciona métodos estáticos para cifrar y descifrar texto de forma segura.
 * Utiliza {@link org.jasypt.util.text.BasicTextEncryptor} internamente.
 *
 * <p>Uso típico:</p>
 * <pre>{@code
 *   String encrypted = Encryptor.encrypt("clave123", "secreto");
 *   String decrypted = Encryptor.decrypt(encrypted, "secreto");
 * }</pre>
 *
 * @author Juan
 * @since 1.0.0
 */
@Slf4j
public class Encryptor {

    /**
     * Constructor privado para evitar instanciación de esta clase utilitaria.
     */
    private Encryptor() { /*    */ }

    /**
     * Cifra el texto usando la clave maestra.
     *
     * @param textoPlano    Texto a cifrar.
     * @param claveMaestra  Clave para cifrar/descifrar (secreta).
     * @return Texto cifrado en Base64.
     * @throws EncryptorException si ocurre un error durante el cifrado.
     */
    public static String encrypt(String textoPlano, String claveMaestra) {
        if (textoPlano == null || claveMaestra == null) {
            throw new EncryptorException("El texto a cifrar y la clave no pueden ser null");
        }
        try {
            log.debug("Iniciando cifrado de texto...");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(claveMaestra);
            String resultado = encryptor.encrypt(textoPlano);
            log.debug("Texto cifrado correctamente.");
            return resultado;
        } catch (Exception e) {
            log.error("Error cifrando el texto: {}", e.getMessage());
            throw new EncryptorException("Error cifrando el texto", e);
        }
    }

    /**
     * Descifra el texto usando la clave maestra.
     *
     * @param textoCifrado  Texto cifrado en Base64.
     * @param claveMaestra  Clave para cifrar/descifrar (secreta).
     * @return Texto descifrado.
     * @throws EncryptorException si ocurre un error durante el descifrado.
     */
    public static String decrypt(String textoCifrado, String claveMaestra) {
        if (textoCifrado == null || claveMaestra == null) {
            throw new EncryptorException("El texto a descifrar y la clave no pueden ser null");
        }
        try {
            log.debug("Iniciando descifrado de texto...");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(claveMaestra);
            String resultado = encryptor.decrypt(textoCifrado);
            log.debug("Texto descifrado correctamente.");
            return resultado;
        } catch (Exception e) {
            log.error("Error descifrando el texto: {}", e.getMessage());
            throw new EncryptorException("Error descifrando el texto", e);
        }
    }
}
