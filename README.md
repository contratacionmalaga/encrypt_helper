# encrypt-helper

Librería Java para cifrar y descifrar texto mediante una clave proporcionada por la aplicación consumidora.

## Requisitos

- Java 21.
- Maven 3.6.3 o superior.

## Dependencia Maven

```xml
<dependency>
    <groupId>local.jarios</groupId>
    <artifactId>encrypt-helper</artifactId>
    <version>6.0.0</version>
</dependency>
```

La librería expone `slf4j-api`, pero no fuerza una implementación de logging en tiempo de ejecución. La aplicación consumidora debe proporcionar la implementación que corresponda.

## Uso básico

Configura siempre la clave desde una fuente externa y segura. No guardes claves reales en el código fuente.

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

También puedes pasar una clave explícita en cada operación:

```java
EncryptorService encryptorService = new EncryptorServiceImpl();

String textoCifrado = encryptorService.encrypt("texto sensible", clave);
String textoDescifrado = encryptorService.decrypt(textoCifrado, clave);
```

## API principal

- `new EncryptorServiceImpl()`: crea el servicio sin clave configurada.
- `new EncryptorServiceImpl(String encryptKey)`: crea el servicio con clave configurada.
- `setEncryptKey(String encryptKey)`: configura la clave por defecto.
- `getEncryptKey()`: devuelve la clave configurada.
- `encryptDefaultKey(String plainText)`: cifra con la clave configurada.
- `decryptDefaultKey(String encryptedText)`: descifra con la clave configurada.
- `encrypt(String plainText, String key)`: cifra con una clave explícita.
- `decrypt(String encryptedText, String key)`: descifra con una clave explícita.

Si se llama a `encryptDefaultKey` o `decryptDefaultKey` sin haber configurado una clave, se lanza `EncryptorException`.

## Seguridad operativa

- No uses claves hardcodeadas.
- No registres claves, textos planos ni textos descifrados en logs.
- Proporciona la clave desde un gestor de secretos, variable de entorno o configuración protegida.
- Rota las claves según la política de seguridad de la aplicación consumidora.
- Si ya existen datos cifrados con versiones anteriores, valida la compatibilidad antes de cambiar el algoritmo o formato.

## Verificación

```bash
./mvnw test
./mvnw verify
./mvnw -Pquality verify
```

El perfil `quality` ejecuta herramientas de análisis estático configuradas en el `pom.xml`.

## Estado criptográfico

La implementación actual usa `org.jasypt.util.text.BasicTextEncryptor` de Jasypt 1.9.3. Antes de usar la librería para secretos productivos de alto impacto, revisa si sus parámetros criptográficos cumplen los requisitos de tu organización y define un plan de migración para futuros cambios de formato.
