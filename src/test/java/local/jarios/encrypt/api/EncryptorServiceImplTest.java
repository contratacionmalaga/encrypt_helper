package local.jarios.encrypt.api;

import local.jarios.encrypt.exception.EncryptorException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EncryptorServiceImplTest {

    private static final String TEST_KEY = "clave-maestra-solo-para-tests";
    private static final String OTHER_TEST_KEY = "otra-clave-solo-para-tests";
    private static final String PLAIN_TEXT = "texto sensible de prueba";

    @Test
    void encryptAndDecryptWithExplicitKey() {
        EncryptorService encryptorService = new EncryptorServiceImpl();

        String encryptedText = encryptorService.encrypt(PLAIN_TEXT, TEST_KEY);
        String decryptedText = encryptorService.decrypt(encryptedText, TEST_KEY);

        assertThat(encryptedText).isNotBlank().isNotEqualTo(PLAIN_TEXT);
        assertThat(decryptedText).isEqualTo(PLAIN_TEXT);
    }

    @Test
    void encryptAndDecryptWithConfiguredKey() {
        EncryptorService encryptorService = new EncryptorServiceImpl(TEST_KEY);

        String encryptedText = encryptorService.encryptDefaultKey(PLAIN_TEXT);
        String decryptedText = encryptorService.decryptDefaultKey(encryptedText);

        assertThat(encryptedText).isNotBlank().isNotEqualTo(PLAIN_TEXT);
        assertThat(decryptedText).isEqualTo(PLAIN_TEXT);
    }

    @Test
    void defaultKeyOperationsFailWhenKeyIsNotConfigured() {
        EncryptorService encryptorService = new EncryptorServiceImpl();

        assertThatThrownBy(() -> encryptorService.encryptDefaultKey(PLAIN_TEXT))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave por defecto no configurada");

        assertThatThrownBy(() -> encryptorService.decryptDefaultKey("texto-cifrado"))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave por defecto no configurada");
    }

    @Test
    void setEncryptKeyRejectsInvalidKeys() {
        EncryptorService encryptorService = new EncryptorServiceImpl();

        assertThatThrownBy(() -> encryptorService.setEncryptKey(null))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave nula o vacía");

        assertThatThrownBy(() -> encryptorService.setEncryptKey("   "))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave nula o vacía");
    }

    @Test
    void encryptRejectsInvalidInput() {
        EncryptorService encryptorService = new EncryptorServiceImpl();

        assertThatThrownBy(() -> encryptorService.encrypt(null, TEST_KEY))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Texto a cifrar nulo o vacío");

        assertThatThrownBy(() -> encryptorService.encrypt("   ", TEST_KEY))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Texto a cifrar nulo o vacío");

        assertThatThrownBy(() -> encryptorService.encrypt(PLAIN_TEXT, null))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave de cifrado nula o vacía");

        assertThatThrownBy(() -> encryptorService.encrypt(PLAIN_TEXT, "   "))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave de cifrado nula o vacía");
    }

    @Test
    void decryptRejectsInvalidInput() {
        EncryptorService encryptorService = new EncryptorServiceImpl();

        assertThatThrownBy(() -> encryptorService.decrypt(null, TEST_KEY))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Texto a descifrar nulo o vacío");

        assertThatThrownBy(() -> encryptorService.decrypt("   ", TEST_KEY))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Texto a descifrar nulo o vacío");

        assertThatThrownBy(() -> encryptorService.decrypt("texto-cifrado", null))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave de descifrado nula o vacía");

        assertThatThrownBy(() -> encryptorService.decrypt("texto-cifrado", "   "))
                .isInstanceOf(EncryptorException.class)
                .hasMessageContaining("Clave de descifrado nula o vacía");
    }

    @Test
    void decryptWithWrongKeyDoesNotExposeOriginalTextWhenOperationSucceeds() {
        EncryptorService encryptorService = new EncryptorServiceImpl();
        String encryptedText = encryptorService.encrypt(PLAIN_TEXT, TEST_KEY);

        try {
            String decryptedText = encryptorService.decrypt(encryptedText, OTHER_TEST_KEY);
            assertThat(decryptedText).isNotEqualTo(PLAIN_TEXT);
        } catch (EncryptorException ex) {
            assertThat(ex).hasMessageContaining("Error inesperado durante el proceso de descifrado");
        }
    }
}
