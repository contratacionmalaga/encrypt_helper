package local.jarios.encryptor.config;

import local.jarios.encryptor.exception.EncryptorException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EncryptorTest {

    private static final String CLAVE = "claveSegura123!";
    private static final String TEXTO = "valorSecreto123";

    @Test
    void testEncryptAndDecrypt_successful() {
        String encrypted = Encryptor.encrypt(TEXTO, CLAVE);
        assertNotNull(encrypted);
        assertNotEquals(TEXTO, encrypted);

        String decrypted = Encryptor.decrypt(encrypted, CLAVE);
        assertEquals(TEXTO, decrypted);
    }

    @Test
    void testEncrypt_textoVacio() {
        String encrypted = Encryptor.encrypt("", CLAVE);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());

        String decrypted = Encryptor.decrypt(encrypted, CLAVE);
        assertEquals("", decrypted);
    }

    @Test
    void testEncrypt_nullTexto() {
        assertThrows(EncryptorException.class, () -> Encryptor.encrypt(null, CLAVE));
    }

    @Test
    void testEncrypt_nullClave() {
        assertThrows(EncryptorException.class, () -> Encryptor.encrypt(TEXTO, null));
    }

    @Test
    void testDecrypt_nullTexto() {
        assertThrows(EncryptorException.class, () -> Encryptor.decrypt(null, CLAVE));
    }

    @Test
    void testDecrypt_claveIncorrecta() {
        String encrypted = Encryptor.encrypt(TEXTO, CLAVE);
        assertThrows(EncryptorException.class, () -> Encryptor.decrypt(encrypted, "otraClaveInvalida"));
    }

    @Test
    void testEncrypt_andDecrypt_claveLarga() {
        String longKey = "claveMuyLargaConCaracteresEspeciales!@#$%&*()_+1234567890";
        String encrypted = Encryptor.encrypt(TEXTO, longKey);
        String decrypted = Encryptor.decrypt(encrypted, longKey);
        assertEquals(TEXTO, decrypted);
    }
}
