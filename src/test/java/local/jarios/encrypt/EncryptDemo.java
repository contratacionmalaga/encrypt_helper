package local.jarios.encrypt;

import local.jarios.encrypt.api.EncryptorService;
import local.jarios.encrypt.api.EncryptorServiceImpl;
import local.jarios.encrypt.exception.EncryptorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de demostracion para ejecutar cifrado y descifrado desde linea de comandos. */
public class EncryptDemo {

  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDemo.class);

  /** Constructor por defecto. */
  public EncryptDemo() {}

  /**
   * Ejecuta la demostracion.
   *
   * @param args parametros de entrada: clave maestra y texto original
   */
  public static void main(String[] args) {
    LOGGER.info("Inicio de la demo de cifrado.");

    if (args.length != 2) {
      LOGGER.error("Uso invalido. Se requieren exactamente 2 parametros: clave maestra y texto.");
      return;
    }

    try {
      String masterKey = args[0];
      String plainText = args[1];
      EncryptorService encryptorService = new EncryptorServiceImpl(masterKey);
      LOGGER.info("Servicio Encryptor creado correctamente.");

      String encryptedWithDefaultKey = encryptorService.encryptDefaultKey(plainText);
      LOGGER.info("Texto cifrado usando clave por defecto correctamente.");

      String encryptedWithExplicitKey = encryptorService.encrypt(plainText, masterKey);
      LOGGER.info("Texto cifrado usando clave personalizada correctamente.");

      encryptorService.decryptDefaultKey(encryptedWithDefaultKey);
      LOGGER.info("Texto descifrado usando clave por defecto correctamente.");

      encryptorService.decrypt(encryptedWithExplicitKey, masterKey);
      LOGGER.info("Texto descifrado usando clave personalizada correctamente.");
      LOGGER.info("Fin de la demo de cifrado.");
    } catch (EncryptorException ex) {
      LOGGER.error("La demo de cifrado ha finalizado con error.");
    }
  }
}
