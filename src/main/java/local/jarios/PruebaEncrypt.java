package local.jarios;

import local.jarios.encryptor.config.Encryptor;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal para ejecutar el cifrado y descifrado desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar encriptador.jar <claveMaestra> <texto>}
 * </p>
 */
@Slf4j
public class EncriptarClave {

    /**
     * Constructor por defecto.
     * Esta clase solo contiene el método main, no se debe instanciar.
     */
    public EncriptarClave() {
        // Constructor vacío
    }

    /**
     * Método principal para ejecutar el cifrado y descifrado.
     *
     * @param args parámetros de entrada: [0] clave maestra, [1] texto original
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("Uso: java Encryptor <claveMaestra> <original>");
            System.exit(1);
        }

        String claveMaestra = args[0];
        String original = args[1];

        try {
            String cifrado = Encryptor.encrypt(original, claveMaestra);
            log.info("Texto cifrado: {}", cifrado);

            String descifrado = Encryptor.decrypt(cifrado, claveMaestra);
            log.info("Texto descifrado: {}", descifrado);

        } catch (Exception e) {
            log.error("Error durante el proceso de cifrado/descifrado", e);
        }
    }
}
