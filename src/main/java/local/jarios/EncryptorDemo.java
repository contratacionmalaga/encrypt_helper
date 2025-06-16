package local.jarios;

import local.jarios.encrypt.api.EncryptorImpl;
import local.jarios.encrypt.api.Encryptor;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal para ejecutar el cifrado y descifrado desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar encriptador.jar <claveMaestra> <texto>}
 * </p>
 */
@Slf4j
public class EncryptorDemo {

    /**
     * Texto original por defecto para cifrar si no se pasa argumento.
     */
    private static final String TEXTO_ORIGINAL_DEFAULT = "BWFZHGPIJKXVNSLU";

    /**
     * Constructor por defecto.
     * Esta clase solo contiene el método main, no se debe instanciar.
     */
    public EncryptorDemo() {
        // Constructor vacío
    }

    /**
     * Función principal para ejecutar el cifrado y descifrado.
     *
     * @param args parámetros de entrada: [0] clave maestra, [1] texto original
     */
    public static void main(String[] args) {

        Encryptor encryptor = new EncryptorImpl();
        log.info("Servicio Encryptor creado correctamente.");

        // Muestra clave por defecto inicial
        log.info("Clave por defecto inicial: {}", encryptor.getDefaultKey());

        String claveMaestra;
        String textoOriginal;

        if (args.length == 2) {
            claveMaestra = args[0];
            textoOriginal = args[1];
            log.info("Parámetros recibidos: clave maestra oculta, texto original: {}", textoOriginal);
        } else {
            claveMaestra = encryptor.getDefaultKey();
            textoOriginal = TEXTO_ORIGINAL_DEFAULT;
            log.info("No se recibieron parámetros válidos. Usando valores por defecto.");
            log.info("Clave maestra por defecto: {}", claveMaestra);
            log.info("Texto original por defecto: {}", textoOriginal);
        }

        try {
            // Cambiamos la clave por defecto a la clave recibida (o la que ya tenía)
            encryptor.setDefaultKey(claveMaestra);
            log.info("Clave por defecto configurada a: {}", encryptor.getDefaultKey());

            // CIFRADO usando clave por defecto (sin pasar la clave explícita)
            String cifradoConClaveDefecto = encryptor.encrypt(textoOriginal);
            log.info("Texto cifrado usando clave por defecto: {}", cifradoConClaveDefecto);

            // CIFRADO usando clave personalizada (pasándola explícitamente)
            String cifradoConClavePersonalizada = encryptor.encrypt(textoOriginal, claveMaestra);
            log.info("Texto cifrado usando clave personalizada: {}", cifradoConClavePersonalizada);

            // DESCIFRADO usando clave por defecto
            String descifradoConClaveDefecto = encryptor.decrypt(cifradoConClaveDefecto);
            log.info("Texto descifrado usando clave por defecto: {}", descifradoConClaveDefecto);

            // DESCIFRADO usando clave personalizada
            String descifradoConClavePersonalizada = encryptor.decrypt(cifradoConClavePersonalizada, claveMaestra);
            log.info("Texto descifrado usando clave personalizada: {}", descifradoConClavePersonalizada);

            // Prueba de envoltorio "ENC(...)"
            String envoltorio = String.format("ENC(%s)", cifradoConClaveDefecto);
            log.info("Texto cifrado con envoltorio: {}", envoltorio);

            // Extraemos el texto cifrado del envoltorio y lo desciframos
            String cifradoSinEnvoltorio = envoltorio.substring(4, envoltorio.length() - 1);
            String descifradoDesdeEnvoltorio = encryptor.decrypt(cifradoSinEnvoltorio);
            log.info("Texto descifrado desde envoltorio usando clave por defecto: {}", descifradoDesdeEnvoltorio);

        } catch (Exception e) {
            log.error("Error en cifrado/descifrado", e);
        }
    }
}
