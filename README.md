# encrypt-helper

Libreria Java para cifrar y descifrar texto mediante una clave proporcionada por la aplicacion consumidora.

## Requisitos

- Java 21. Se recomienda usar el ultimo parche disponible de la familia Java 21 LTS.
- Maven 3.6.3 o superior.
- Maven Wrapper incluido. El wrapper usa Maven 3.9.16.

## Dependencia Maven

```xml
<dependency>
  <groupId>local.jarios</groupId>
  <artifactId>encrypt-helper</artifactId>
  <version>7.0.1</version>
</dependency>
```

La libreria expone `slf4j-api`, pero no fuerza una implementacion de logging en tiempo de ejecucion. La aplicacion consumidora debe proporcionar la implementacion que corresponda.

## Uso basico

Configura siempre la clave desde una fuente externa y segura. No guardes claves reales en el codigo fuente.

```java
import local.jarios.encrypt.api.EncryptorService;
import local.jarios.encrypt.api.EncryptorServiceImpl;

public class EjemploEncryptHelper {

  public static void main(String[] args) {
    String clave = System.getenv("ENCRYPT_HELPER_KEY");
    String textoPlano = "texto sensible";

    EncryptorService encryptorService = new EncryptorServiceImpl(clave);

    String textoCifrado = encryptorService.encryptDefaultKey(textoPlano);
    String textoDescifrado = encryptorService.decryptDefaultKey(textoCifrado);
  }
}
```

Tambien puedes pasar una clave explicita en cada operacion:

```java
EncryptorService encryptorService = new EncryptorServiceImpl();

String textoCifrado = encryptorService.encrypt("texto sensible", clave);
String textoDescifrado = encryptorService.decrypt(textoCifrado, clave);
```

## API principal

- `new EncryptorServiceImpl()`: crea el servicio sin clave configurada.
- `new EncryptorServiceImpl(String encryptKey)`: crea el servicio con clave configurada.
- `setEncryptKey(String encryptKey)`: configura la clave por defecto.
- `hasEncryptKeyConfigured()`: indica si hay una clave configurada sin exponerla.
- `encryptDefaultKey(String plainText)`: cifra con la clave configurada.
- `decryptDefaultKey(String encryptedText)`: descifra con la clave configurada.
- `encrypt(String plainText, String key)`: cifra con una clave explicita.
- `decrypt(String encryptedText, String key)`: descifra con una clave explicita.

Si se llama a `encryptDefaultKey` o `decryptDefaultKey` sin haber configurado una clave, se lanza `EncryptorException`.

## Formato criptografico

La version 7 genera nuevos cifrados con formato `EH2(...)`. Internamente usa:

- AES-GCM (`AES/GCM/NoPadding`) con etiqueta de autenticacion de 128 bits.
- PBKDF2-HMAC-SHA256 para derivar la clave.
- Salt aleatorio de 16 bytes.
- IV aleatorio de 12 bytes.
- 210.000 iteraciones PBKDF2.
- Carga serializada con Base64 URL-safe sin padding.

Para compatibilidad, `decrypt` tambien acepta textos antiguos sin prefijo `EH2(` y los descifra con Jasypt `BasicTextEncryptor`. Los nuevos cifrados no usan Jasypt.

## Cambios incompatibles en 7.0.0

- `getEncryptKey()` se elimina de la API publica para no exponer claves en claro.
- Se anade `hasEncryptKeyConfigured()` como sustituto seguro.
- El formato nuevo de cifrado cambia a `EH2(...)`.
- El texto plano vacio o compuesto por espacios pasa a ser cifrable; solo `null` es invalido como payload.

## Seguridad operativa

- No uses claves hardcodeadas.
- No registres claves, textos planos ni textos descifrados en logs.
- Proporciona la clave desde un gestor de secretos, variable de entorno o configuracion protegida.
- Rota las claves segun la politica de seguridad de la aplicacion consumidora.
- Valida la compatibilidad antes de retirar el descifrado legado si existen datos cifrados con versiones anteriores.

## Verificacion

```bash
./mvnw test
./mvnw -Pquality verify
```

El perfil `quality` ejecuta herramientas de analisis estatico configuradas en el `pom.xml` y bloquea el build si Checkstyle encuentra violaciones.
