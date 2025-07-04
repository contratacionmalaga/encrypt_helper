package local.jarios.encrypt.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ayudante de los String
 * @author Juan Antonio
 */
public final class StringHelper {

    /**
     * Instancia única (singleton) del gestor de propiedades.
     * Inicialización temprana y thread-safe mediante static final.
     */
    private static final Logger LOGGER = LogManager.getLogger("local.jarios.encrypt");

    /**
     * Constructro privado de la clase -- Evita es instanciamiento
     */
    private StringHelper() {

        // Constructor vacío
    }

    /**
     * Validador de cadena
     * @param cadena a validar
     * @return booleano indicando si la cadena es válida o no
     */
    public static boolean isInvalidString(String cadena) {

        if (cadena == null) {
            LOGGER.debug("[isInvalidString] - La cadena es NULL. Cadena: {}", cadena);
            return true;
        }

        if (cadena.isBlank()) {
            LOGGER.debug("[isInvalidString] - La cadena es BLANK. Cadena: {}", cadena);
            return true;
        }

        LOGGER.debug("[isInvalidString] - La cadena no es NULL ni BLANK. Cadena: {}", cadena);
        return false;

    }
}
