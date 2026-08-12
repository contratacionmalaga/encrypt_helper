package local.jarios.encrypt.api;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import local.jarios.encrypt.exception.EncryptorException;
import local.jarios.encrypt.helpers.StringHelper;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion de {@link EncryptorService} con cifrado versionado.
 *
 * <p>Los nuevos cifrados se generan con formato {@code EH2(...)} usando AES-GCM y PBKDF2.
 * Los textos sin prefijo se descifran con Jasypt para mantener compatibilidad legado.
 *
 * @author Juan
 * @since 1.0.0
 */
public final class EncryptorServiceImpl implements EncryptorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptorServiceImpl.class);
  private static final String CURRENT_PREFIX = "EH2";
  private static final String FORMAT_START = CURRENT_PREFIX + "(";
  private static final String FORMAT_END = ")";
  private static final String DELIMITER = ".";
  private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
  private static final int SALT_BYTES = 16;
  private static final int IV_BYTES = 12;
  private static final int TAG_BITS = 128;
  private static final int KEY_BITS = 256;
  private static final int PBKDF2_ITERATIONS = 210_000;

  private final SecureRandom secureRandom;
  private String encryptKey;

  /**
   * Constructor por defecto. La clave debe configurarse explicitamente.
   */
  public EncryptorServiceImpl() {
    this(new SecureRandom());
  }

  /**
   * Constructor que configura la clave de cifrado.
   *
   * @param encryptKey clave de cifrado
   * @throws EncryptorException si la clave es nula o blanca
   */
  public EncryptorServiceImpl(String encryptKey) throws EncryptorException {
    this();
    setEncryptKey(encryptKey);
  }

  private EncryptorServiceImpl(SecureRandom secureRandom) {
    this.secureRandom = Objects.requireNonNull(secureRandom, "secureRandom");
  }

  @Override
  public void setEncryptKey(String encryptKey) throws EncryptorException {
    validateKey(encryptKey, "Clave nula o vacia no permitida.");
    this.encryptKey = encryptKey;
    LOGGER.debug("Clave de cifrado configurada correctamente.");
  }

  @Override
  public boolean hasEncryptKeyConfigured() {
    return !StringHelper.isNullOrBlank(this.encryptKey);
  }

  @Override
  public String encryptDefaultKey(String plainText) throws EncryptorException {
    ensureEncryptKeyConfigured();
    return encrypt(plainText, this.encryptKey);
  }

  @Override
  public String encrypt(String plainText, String key) throws EncryptorException {
    validatePayload(plainText, "Texto a cifrar nulo.");
    validateKey(key, "Clave de cifrado nula o vacia.");

    try {
      byte[] salt = randomBytes(SALT_BYTES);
      byte[] iv = randomBytes(IV_BYTES);
      SecretKeySpec secretKey = deriveKey(key, salt);
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_BITS, iv));
      byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      LOGGER.info("Texto cifrado correctamente.");
      return encodeVersioned(salt, iv, cipherText);
    } catch (GeneralSecurityException ex) {
      String msg = "Error inesperado durante el proceso de cifrado.";
      LOGGER.debug(msg, ex);
      throw new EncryptorException(msg, ex);
    }
  }

  @Override
  public String decryptDefaultKey(String encryptedText) throws EncryptorException {
    ensureEncryptKeyConfigured();
    return decrypt(encryptedText, this.encryptKey);
  }

  @Override
  public String decrypt(String encryptedText, String key) throws EncryptorException {
    validateEncryptedText(encryptedText);
    validateKey(key, "Clave de descifrado nula o vacia.");

    if (!isCurrentFormat(encryptedText)) {
      return decryptLegacy(encryptedText, key);
    }

    try {
      String[] parts = splitVersionedPayload(encryptedText);
      byte[] salt = decode(parts[0]);
      byte[] iv = decode(parts[1]);
      byte[] cipherText = decode(parts[2]);
      SecretKeySpec secretKey = deriveKey(key, salt);
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_BITS, iv));
      String plainText = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
      LOGGER.info("Texto descifrado correctamente.");
      return plainText;
    } catch (AEADBadTagException ex) {
      String msg = "Texto cifrado no autentico o clave incorrecta.";
      LOGGER.debug(msg, ex);
      throw new EncryptorException(msg, ex);
    } catch (IllegalArgumentException | GeneralSecurityException ex) {
      String msg = "Error inesperado durante el proceso de descifrado.";
      LOGGER.debug(msg, ex);
      throw new EncryptorException(msg, ex);
    }
  }

  private static void validatePayload(String plainText, String message) {
    if (StringHelper.isNull(plainText)) {
      throw new EncryptorException(message);
    }
  }

  private static void validateEncryptedText(String encryptedText) {
    if (StringHelper.isNullOrBlank(encryptedText)) {
      throw new EncryptorException("Texto a descifrar nulo o vacio.");
    }
  }

  private static void validateKey(String key, String message) {
    if (StringHelper.isNullOrBlank(key)) {
      throw new EncryptorException(message);
    }
  }

  private void ensureEncryptKeyConfigured() throws EncryptorException {
    if (!hasEncryptKeyConfigured()) {
      throw new EncryptorException("Clave por defecto no configurada.");
    }
  }

  private byte[] randomBytes(int length) {
    byte[] bytes = new byte[length];
    this.secureRandom.nextBytes(bytes);
    return bytes;
  }

  private static SecretKeySpec deriveKey(String key, byte[] salt) throws GeneralSecurityException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
    KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_BITS);
    byte[] encoded = factory.generateSecret(spec).getEncoded();
    return new SecretKeySpec(encoded, "AES");
  }

  private static String encodeVersioned(byte[] salt, byte[] iv, byte[] cipherText) {
    return FORMAT_START
        + encode(salt)
        + DELIMITER
        + encode(iv)
        + DELIMITER
        + encode(cipherText)
        + FORMAT_END;
  }

  private static boolean isCurrentFormat(String encryptedText) {
    return encryptedText.startsWith(FORMAT_START) && encryptedText.endsWith(FORMAT_END);
  }

  private static String[] splitVersionedPayload(String encryptedText) {
    String payload = encryptedText.substring(FORMAT_START.length(), encryptedText.length() - 1);
    String[] parts = payload.split("\\.", -1);
    if (parts.length != 3) {
      throw new EncryptorException("Formato de texto cifrado no soportado.");
    }
    return parts;
  }

  private static String encode(byte[] value) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
  }

  private static byte[] decode(String value) {
    return Base64.getUrlDecoder().decode(value);
  }

  private static String decryptLegacy(String encryptedText, String key) {
    try {
      BasicTextEncryptor encryptor = new BasicTextEncryptor();
      encryptor.setPassword(key);
      String plainText = encryptor.decrypt(encryptedText);
      LOGGER.info("Texto descifrado con formato legado correctamente.");
      return plainText;
    } catch (Exception ex) {
      String msg = "Error inesperado durante el proceso de descifrado legado.";
      LOGGER.debug(msg, ex);
      throw new EncryptorException(msg, ex);
    }
  }
}
