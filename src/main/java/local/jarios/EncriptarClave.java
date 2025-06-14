package local.jarios;

import local.jarios.email.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

import static local.jarios.email.config.Encryptor.encrypt;

@Slf4j
public class EncriptarClave {

    /**
     *
     * @param args parámetros
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            log.info("Uso: java Encryptor <clavePlana>");
            System.exit(1);
        }
        String claveMaestra = Constantes.CLAVE_MAESTRA;
        String textoPlano = args[0];
        String textoCifrado = encrypt(textoPlano, claveMaestra);
        log.info("Texto cifrado: {}", textoCifrado);
    }
}
