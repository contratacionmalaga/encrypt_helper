package local.jarios.encrypt.helpers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringHelperTest {

    @Test
    void nullStringIsInvalid() {
        assertThat(StringHelper.isInvalidString(null)).isTrue();
    }

    @Test
    void blankStringIsInvalid() {
        assertThat(StringHelper.isInvalidString("   ")).isTrue();
    }

    @Test
    void textWithContentIsValid() {
        assertThat(StringHelper.isInvalidString("contenido")).isFalse();
    }
}
