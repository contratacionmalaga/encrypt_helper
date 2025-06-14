package local.jarios;

import local.jarios.encrypt.config.Decrypt;
import local.jarios.encrypt.config.Encrypt;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal para ejecutar el cifrado y descifrado desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar encriptador.jar <claveMaestra> <texto>}
 * </p>
 */
@Slf4j
public class PruebaEncrypt {

    /**
     * Clave maestra por defecto usada para cifrar/descifrar si no se pasa argumento.
     */
    private static final String CLAVE_MAESTRA_DEFAULT = "Malaga$$2025";

    /**
     * Texto original por defecto para cifrar si no se pasa argumento.
     */
    private static final String TEXTO_ORIGINAL_DEFAULT = "BWFZHGPIJKXVNSLU";

    /**
     * Constructor por defecto.
     * Esta clase solo contiene el método main, no se debe instanciar.
     */
    public PruebaEncrypt() {
        // Constructor vacío
    }

    /**
     * Método principal para ejecutar el cifrado y descifrado.
     *
     * @param args parámetros de entrada: [0] clave maestra, [1] texto original
     */
    public static void main(String[] args) {

        final String claveMaestra;
        final String original;

        if (args.length != 2) {
            log.warn("No se recibieron los parámetros esperados. Se requieren 2 argumentos: <clave maestra> <texto original>");
            log.info("Ejemplo: java PruebaEncrypt Malaga$$2025 MiTextoSecreto");
            log.info("Usando valores por defecto para demostración.");
            claveMaestra = CLAVE_MAESTRA_DEFAULT;
            original = TEXTO_ORIGINAL_DEFAULT;
        } else {
            claveMaestra = args[0];
            original = args[1];
            log.info("Parámetros recibidos. Clave Maestra: *****, Texto Original: {}", original);
        }

        try {
            String cifrado = Encrypt.encrypt(original, claveMaestra);
            log.info("Texto cifrado: {}", cifrado);

            String descifrado = Decrypt.decrypt(cifrado, claveMaestra);
            log.info("Texto descifrado: {}", descifrado);

        } catch (Exception e) {
            log.error("Error durante el proceso de cifrado/descifrado", e);
        }
    }
}
