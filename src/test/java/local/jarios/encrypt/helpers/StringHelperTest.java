package local.jarios.encrypt.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void nullStringIsNull() {
    assertThat(StringHelper.isNull(null)).isTrue();
  }

  @Test
  void emptyStringIsNotNull() {
    assertThat(StringHelper.isNull("")).isFalse();
  }

  @Test
  void nullStringIsBlankInvalid() {
    assertThat(StringHelper.isNullOrBlank(null)).isTrue();
  }

  @Test
  void blankStringIsBlankInvalid() {
    assertThat(StringHelper.isNullOrBlank("   ")).isTrue();
  }

  @Test
  void textWithContentIsValid() {
    assertThat(StringHelper.isNullOrBlank("contenido")).isFalse();
  }
}
