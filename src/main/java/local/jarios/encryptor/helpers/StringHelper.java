package local.jarios.encryptor.helpers;

/**
 * Ayudante de los String
 * @author Juan Antonio
 */
public final class StringHelper {

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

        return ((cadena == null) || (cadena.isEmpty()));
    }
}
