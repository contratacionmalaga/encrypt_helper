package local.jarios.encrypt.config;

import local.jarios.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Clase utilitaria para cifrado y descifrado de texto usando Jasypt con una clave maestra.
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
public class Encrypt {

    /**
     * Constructor privado para evitar instanciación de esta clase utilitaria.
     */
    private Encrypt() { /*    */ }

    /**
     * Cifra el texto usando la clave maestra.
     *
     * @param textoPlano    Texto a cifrar.
     * @param claveMaestra  Clave para cifrar/descifrar (secreta).
     * @return Texto cifrado en Base64.
     * @throws EncryptException si ocurre un error durante el cifrado.
     */
    public static String encrypt(String textoPlano, String claveMaestra) {
        if (textoPlano == null || claveMaestra == null) {
            throw new EncryptException("El texto a cifrar y la clave no pueden ser null");
        }
        try {
            log.debug("Iniciando cifrado de texto...");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(claveMaestra);
            log.debug("Clave maestra: {}", claveMaestra);
            String resultado = encryptor.encrypt(textoPlano);
            log.debug("Original: {}", textoPlano);
            log.debug("Texto Cifrado: {}", resultado);
            log.debug("Texto cifrado correctamente.");
            return resultado;
        } catch (Exception e) {
            log.error("Error cifrando el texto: {}", e.getMessage());
            throw new EncryptException("Error cifrando el texto", e);
        }
    }
}
