package local.jarios.encrypt.helpers;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase utilitaria para validación y manipulación de {@link String}.
 * <p>
 * Proporciona métodos estáticos para comprobar si una cadena es nula o vacía.
 * No se debe instanciar.
 * </p>
 *
 * Autor: Juan Antonio
 */
@Slf4j
public final class StringHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private StringHelper() {
        log.debug("[Constructor] - StringHelper no puede ser instanciado.");
    }

    /**
     * Valida si una cadena es nula o está vacía (blank).
     *
     * @param cadena Cadena a validar
     * @return {@code true} si la cadena es nula o blank, {@code false} en caso contrario
     */
    public static boolean isInvalidString(String cadena) {

        if (cadena == null) {
            log.debug("[isInvalidString] - La cadena es NULL.");
            return true;
        }

        if (cadena.isBlank()) {
            log.debug("[isInvalidString] - La cadena es BLANK.");
            return true;
        }

        log.debug("[isInvalidString] - La cadena es válida. Cadena: '{}'", cadena);
        return false;
    }
}
