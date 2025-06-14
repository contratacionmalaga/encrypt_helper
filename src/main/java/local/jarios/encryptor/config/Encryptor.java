package local.jarios.email.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Clase auxiliar para cifrar texto con Jasypt.
 * Útil para generar las contraseñas cifradas que se almacenarán en propiedades.
 */
@Slf4j
public class Encryptor {

    /**
     * Cifra el texto usando la clave maestra.
     *
     * @param textoPlano Texto a cifrar.
     * @param claveMaestra Clave para cifrar/descifrar (secreta).
     * @return Texto cifrado en Base64.
     */
    public static String encrypt(String textoPlano, String claveMaestra) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(claveMaestra);
        return encryptor.encrypt(textoPlano);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java Encryptor <claveMaestra> <textoPlano>");
            System.exit(1);
        }
        String claveMaestra = args[0];
        String textoPlano = args[1];
        String textoCifrado = encrypt(textoPlano, claveMaestra);
        log.info("Texto cifrado: {}", textoCifrado);
    }
}
