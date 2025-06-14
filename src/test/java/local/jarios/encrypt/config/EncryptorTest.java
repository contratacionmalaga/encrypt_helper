package local.jarios.encrypt.config;

import local.jarios.encrypt.exception.EncryptException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EncryptorTest {

    private static final String CLAVE = "claveSegura123!";
    private static final String TEXTO = "valorSecreto123";

    @Test
    void testEncryptAndDecrypt_successful() {
        String encrypted = Encrypt.encrypt(TEXTO, CLAVE);
        assertNotNull(encrypted);
        assertNotEquals(TEXTO, encrypted);

        String decrypted = Decrypt.decrypt(encrypted, CLAVE);
        assertEquals(TEXTO, decrypted);
    }

    @Test
    void testEncrypt_textoVacio() {
        String encrypted = Encrypt.encrypt("", CLAVE);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());

        String decrypted = Decrypt.decrypt(encrypted, CLAVE);
        assertEquals("", decrypted);
    }

    @Test
    void testEncrypt_nullTexto() {
        assertThrows(EncryptException.class, () -> Encrypt.encrypt(null, CLAVE));
    }

    @Test
    void testEncrypt_nullClave() {
        assertThrows(EncryptException.class, () -> Encrypt.encrypt(TEXTO, null));
    }

    @Test
    void testDecrypt_nullTexto() {
        assertThrows(EncryptException.class, () -> Decrypt.decrypt(null, CLAVE));
    }

    @Test
    void testDecrypt_claveIncorrecta() {
        String encrypted = Encrypt.encrypt(TEXTO, CLAVE);
        assertThrows(EncryptException.class, () -> Decrypt.decrypt(encrypted, "otraClaveInvalida"));
    }

    @Test
    void testEncrypt_andDecrypt_claveLarga() {
        String longKey = "claveMuyLargaConCaracteresEspeciales!@#$%&*()_+1234567890";
        String encrypted = Encrypt.encrypt(TEXTO, longKey);
        String decrypted = Decrypt.decrypt(encrypted, longKey);
        assertEquals(TEXTO, decrypted);
    }
}
