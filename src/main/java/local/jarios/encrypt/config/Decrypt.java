package local.jarios.encrypt.config;

import local.jarios.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Clase utilitaria para cifrado y descifrado de texto usando Jasypt con una clave maestra.
 * Esta clase proporciona métodos estáticos para cifrar y descifrar texto de forma segura.
 * Utiliza {@link BasicTextEncryptor} internamente.
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
public class Decrypt {

    /**
     * Constructor privado para evitar instanciación de esta clase utilitaria.
     */
    private Decrypt() { /*    */ }

    /**
     * Descifra el texto usando la clave maestra.
     *
     * @param textoCifrado  Texto cifrado en Base64.
     * @param claveMaestra  Clave para cifrar/descifrar (secreta).
     * @return Texto descifrado.
     * @throws EncryptException si ocurre un error durante el descifrado.
     */
    public static String decrypt(String textoCifrado, String claveMaestra) {
        if (textoCifrado == null || claveMaestra == null) {
            throw new EncryptException("El texto a descifrar y la clave no pueden ser null");
        }
        try {
            log.debug("Iniciando descifrado de texto...");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(claveMaestra);
            log.debug("Clave maestra: {}", claveMaestra);
            String resultado = encryptor.decrypt(textoCifrado);
            log.debug("Texto cifrado: {}", textoCifrado);
            log.debug("Texto plano: {}", resultado);
            log.debug("Texto descifrado correctamente.");
            return resultado;
        } catch (Exception e) {
            log.error("Error descifrando el texto: {}", e.getMessage());
            throw new EncryptException("Error descifrando el texto", e);
        }
    }
}

