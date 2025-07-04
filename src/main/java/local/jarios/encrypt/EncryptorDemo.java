package local.jarios.encrypt;

import local.jarios.encrypt.api.EncryptorServiceImpl;
import local.jarios.encrypt.api.EncryptorService;
import local.jarios.encrypt.common.util.Mensajes;
import local.jarios.encrypt.enums.TipoFinalEjecucion;
import local.jarios.encrypt.exception.EncryptorException;
import local.jarios.encrypt.helpers.FinalDelProgramaHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * LOGGER del componente
     */
    private static final Logger LOGGER = LogManager.getLogger("local.jarios.encryptor");

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

        // Inicio del log
        LOGGER.info(Mensajes.INICIO);

        EncryptorService encryptorService = new EncryptorServiceImpl();
        log.info("Servicio Encryptor creado correctamente.");

        // Muestra clave por defecto inicial
        log.info("Clave por defecto inicial: {}", encryptorService.getEncryptKey());

        String claveMaestra;
        String textoOriginal;

        if (args.length == 2) {
            claveMaestra = args[0];
            textoOriginal = args[1];
            log.info("Parámetros recibidos: clave maestra oculta, texto original: {}", textoOriginal);
        } else {
            claveMaestra = encryptorService.getEncryptKey();
            textoOriginal = TEXTO_ORIGINAL_DEFAULT;
            log.info("El número de parámetros es distinto de 2. Se utilizarán valores por defecto.");
            log.info("Clave maestra por defecto: {}", claveMaestra);
            log.info("Texto original por defecto: {}", textoOriginal);
        }

        try {
            // Cambiamos la clave por defecto a la clave recibida (o la que ya tenía)
            encryptorService.setEncryptKey(claveMaestra);
            log.info("Clave por defecto configurada a: {}", encryptorService.getEncryptKey());

            // CIFRADO usando clave por defecto (sin pasar la clave explícita)
            String cifradoConClaveDefecto = encryptorService.encryptDefaultKey(textoOriginal);
            log.info("Texto cifrado usando clave por defecto: {}", cifradoConClaveDefecto);

            // CIFRADO usando clave personalizada (pasándola explícitamente)
            String cifradoConClavePersonalizada = encryptorService.encrypt(textoOriginal, claveMaestra);
            log.info("Texto cifrado usando clave personalizada: {}", cifradoConClavePersonalizada);

            // DESCIFRADO usando clave por defecto
            String descifradoConClaveDefecto = encryptorService.decryptDefaultKey(cifradoConClaveDefecto);
            log.info("Texto descifrado usando clave por defecto: {}", descifradoConClaveDefecto);

            // DESCIFRADO usando clave personalizada
            String descifradoConClavePersonalizada = encryptorService.decrypt(cifradoConClavePersonalizada, claveMaestra);
            log.info("Texto descifrado usando clave personalizada: {}", descifradoConClavePersonalizada);

            // Prueba de envoltorio "ENC(...)"
            String envoltorio = String.format("ENC(%s)", cifradoConClaveDefecto);
            log.info("Texto cifrado con envoltorio: {}", envoltorio);

            // Extraemos el texto cifrado del envoltorio y lo desciframos
            String cifradoSinEnvoltorio = envoltorio.substring(4, envoltorio.length() - 1);
            String descifradoDesdeEnvoltorio = encryptorService.decrypt(cifradoSinEnvoltorio, claveMaestra);
            log.info("Texto descifrado desde envoltorio usando clave por defecto: {}", descifradoDesdeEnvoltorio);
            FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.CORRECTO);

        } catch (EncryptorException ex) {

            FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.ERROR);

        }
    }
}
