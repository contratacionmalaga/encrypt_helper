package local.jarios.encrypt;

import local.jarios.encrypt.api.EncryptorService;
import local.jarios.encrypt.api.EncryptorServiceImpl;
import local.jarios.encrypt.exception.EncryptorException;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal para ejecutar el cifrado y descifrado desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar encriptador.jar <claveMaestra> <texto>}
 * </p>
 */
@Slf4j
public class EncryptDemo {

    /**
     * Constructor por defecto.
     * Esta clase solo contiene el método main, no se debe instanciar.
     */
    public EncryptDemo() {
        // Constructor vacío
    }

    /**
     * Función principal para ejecutar el cifrado y descifrado.
     *
     * @param args parámetros de entrada: [0] clave maestra, [1] texto original
     */
    public static void main(String[] args) {

        log.info("Inicio de la demo de cifrado.");

        if (args.length != 2) {
            log.error("Uso inválido. Se requieren exactamente 2 parámetros: clave maestra y texto.");
            return;
        }

        try {
            String claveMaestra = args[0];
            String textoOriginal = args[1];
            EncryptorService encryptorService = new EncryptorServiceImpl(claveMaestra);
            log.info("Servicio Encryptor creado correctamente.");

            // CIFRADO usando clave por defecto (sin pasar la clave explícita)
            String cifradoConClaveDefecto = encryptorService.encryptDefaultKey(textoOriginal);
            log.info("Texto cifrado usando clave por defecto correctamente.");

            // CIFRADO usando clave personalizada (pasándola explícitamente)
            String cifradoConClavePersonalizada = encryptorService.encrypt(textoOriginal, claveMaestra);
            log.info("Texto cifrado usando clave personalizada correctamente.");

            // DESCIFRADO usando clave por defecto
            encryptorService.decryptDefaultKey(cifradoConClaveDefecto);
            log.info("Texto descifrado usando clave por defecto correctamente.");

            // DESCIFRADO usando clave personalizada
            encryptorService.decrypt(cifradoConClavePersonalizada, claveMaestra);
            log.info("Texto descifrado usando clave personalizada correctamente.");

            // Prueba de envoltorio "ENC(...)"
            String envoltorio = String.format("ENC(%s)", cifradoConClaveDefecto);
            log.info("Texto cifrado con envoltorio correctamente.");

            // Extraemos el texto cifrado del envoltorio y lo desciframos
            String cifradoSinEnvoltorio = envoltorio.substring(4, envoltorio.length() - 1);
            encryptorService.decrypt(cifradoSinEnvoltorio, claveMaestra);
            log.info("Texto descifrado desde envoltorio usando clave por defecto correctamente.");
            log.info("Fin de la demo de cifrado.");

        } catch (EncryptorException ex) {

            log.error("La demo de cifrado ha finalizado con error.");

        }
    }
}
