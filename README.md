# Biblioteca de Cifrado y Descifrado de Texto

Biblioteca Java para proteger textos mediante cifrado autenticado, con claves proporcionadas por la aplicación consumidora y compatibilidad de lectura con datos cifrados mediante versiones anteriores.

**Artefacto Maven:** `local.jarios:encrypt-helper:7.1.0`

**Repositorio:** [contratacionmalaga/encrypt_helper](https://github.com/contratacionmalaga/encrypt_helper)

## Información de la versión

| Característica | Configuración |
|---|---|
| Versión del proyecto | **7.1.0** |
| Tipo de artefacto | Biblioteca `jar` |
| Parent Maven | `local.jarios:jarios-parent:1.0.15` |
| Java de referencia y compilación | **21** |
| Maven mínimo configurado en el parent | **3.9.16** |
| Maven distribuido mediante Wrapper | **3.9.16** |
| Maven Wrapper | **3.3.4** |
| Codificación del texto | UTF-8 |
| Formato de los nuevos cifrados | `EH2(...)` |
| Destino de publicación configurado | GitHub Packages |

La versión **7.1.0** adopta `jarios-parent:1.0.15`, actualiza las herramientas de construcción y calidad y utiliza `actions/setup-java@v6.0.1`. Mantiene la API y el formato criptográfico existentes. Su disponibilidad en GitHub Packages depende de la publicación.

El inventario de dependencias y herramientas corresponde al parent **1.0.15** declarado actualmente. Las versiones de otras revisiones del parent no se incorporan automáticamente.

## Alcance y funcionamiento

La biblioteca ofrece dos formas de uso: configurar una clave por defecto en el servicio o proporcionar una clave explícita en cada operación. No configura ninguna clave implícita.

Los nuevos textos se cifran con AES-GCM y se serializan en un formato versionado. Al descifrar, el servicio reconoce el envoltorio `EH2(...)`; las demás entradas se intentan procesar con Jasypt para conservar la compatibilidad con el formato legado.

Es una biblioteca para integrar en aplicaciones Java. No incorpora una interfaz de línea de comandos, almacenamiento de secretos ni rotación automática de claves.

## Requisitos e integración Maven

Se necesita Java 21 o un entorno compatible. Para compilar el proyecto se requiere un JDK y Maven 3.9.16 o superior; el Wrapper incluido proporciona esa versión de Maven.

Declare la dependencia en el POM de la aplicación consumidora:

```xml
<dependency>
  <groupId>local.jarios</groupId>
  <artifactId>encrypt-helper</artifactId>
  <version>7.1.0</version>
</dependency>
```

Si el parent de la aplicación ya gestiona esta dependencia, puede omitir `<version>` y utilizar la versión gestionada por dicho parent. `jarios-parent:1.0.15` todavía gestiona `encrypt-helper:7.0.5`; para consumir **7.1.0** con ese parent, mantenga la versión explícita del ejemplo o sobrescriba la propiedad `encrypt-helper.version` en la aplicación.

### Acceso a GitHub Packages

Para resolver la biblioteca y su parent, incorpore los siguientes repositorios al POM o a un perfil activo de la configuración Maven:

```xml
<repositories>
  <repository>
    <id>github-encrypt-helper</id>
    <url>https://maven.pkg.github.com/contratacionmalaga/encrypt_helper</url>
  </repository>
  <repository>
    <id>github-jarios-parent</id>
    <url>https://maven.pkg.github.com/contratacionmalaga/jarios-parent</url>
  </repository>
</repositories>
```

Añada los servidores correspondientes a su `settings.xml` de Maven, conservando cualquier configuración existente:

```xml
<servers>
  <server>
    <id>github-encrypt-helper</id>
    <username>${env.GITHUB_ACTOR}</username>
    <password>${env.PACKAGES_TOKEN}</password>
  </server>
  <server>
    <id>github-jarios-parent</id>
    <username>${env.GITHUB_ACTOR}</username>
    <password>${env.PACKAGES_TOKEN}</password>
  </server>
</servers>
```

Defina `GITHUB_ACTOR` con el usuario de GitHub y `PACKAGES_TOKEN` con una credencial que tenga acceso de lectura a los paquetes. Los identificadores de servidor deben coincidir con los de los repositorios. Mantenga las credenciales fuera del repositorio.

## Uso básico

### Servicio con clave por defecto

La aplicación obtiene la clave y la entrega al servicio. `ENCRYPT_HELPER_KEY` es el nombre elegido en este ejemplo; la biblioteca no lee variables de entorno automáticamente.

```java
import local.jarios.encrypt.api.EncryptorService;
import local.jarios.encrypt.api.EncryptorServiceImpl;

public final class EjemploCifrado {

  public static void main(String[] args) {
    String clave = System.getenv("ENCRYPT_HELPER_KEY");
    if (clave == null || clave.isBlank()) {
      throw new IllegalStateException("Falta configurar ENCRYPT_HELPER_KEY");
    }

    EncryptorService servicio = new EncryptorServiceImpl(clave);
    String cifrado = servicio.encryptDefaultKey("Texto sensible de ejemplo");
    String recuperado = servicio.decryptDefaultKey(cifrado);

    // Utilice el resultado sin registrar la clave ni el texto recuperado.
  }
}
```

### Clave explícita por operación

```java
EncryptorService servicio = new EncryptorServiceImpl();

String cifrado = servicio.encrypt("Texto sensible de ejemplo", clave);
String recuperado = servicio.decrypt(cifrado, clave);
```

Estas operaciones no requieren configurar una clave por defecto y no modifican la que pudiera tener el servicio.

## API pública

| Constructor o método | Comportamiento |
|---|---|
| `new EncryptorServiceImpl()` | Crea un servicio sin clave por defecto. |
| `new EncryptorServiceImpl(String encryptKey)` | Crea un servicio con la clave indicada. |
| `setEncryptKey(String encryptKey)` | Establece o sustituye la clave por defecto. |
| `hasEncryptKeyConfigured()` | Indica si existe una clave configurada, sin devolverla. |
| `encryptDefaultKey(String plainText)` | Cifra con la clave por defecto. |
| `decryptDefaultKey(String encryptedText)` | Descifra con la clave por defecto. |
| `encrypt(String plainText, String key)` | Cifra con la clave explícita. |
| `decrypt(String encryptedText, String key)` | Descifra con la clave explícita. |

### Validación y errores

Los errores de validación y los fallos de las operaciones criptográficas se comunican mediante `EncryptorException`, que extiende `RuntimeException`.

| Entrada o situación | Resultado |
|---|---|
| Texto plano `null` | Se rechaza. |
| Texto plano vacío o compuesto solo por espacios | Se cifra y se recupera conservando su contenido. |
| Clave `null`, vacía o compuesta solo por espacios | Se rechaza. |
| Texto cifrado `null`, vacío o compuesto solo por espacios | Se rechaza. |
| Operación con clave por defecto sin haberla configurado | Se rechaza. |
| Clave incorrecta o fallo de autenticación en AES-GCM | El descifrado falla. |
| Entrada ilegible o formato incorrecto | El descifrado falla. |

La biblioteca no recorta los espacios de una clave válida. La aplicación debe proporcionar exactamente la misma clave para recuperar los datos.

## Formato criptográfico

| Parámetro | Valor implementado |
|---|---|
| Cifrado | AES de 256 bits en modo GCM |
| Transformación Java | `AES/GCM/NoPadding` |
| Derivación de clave | `PBKDF2WithHmacSHA256` |
| Iteraciones | 210.000 |
| Salt | 16 bytes aleatorios por operación |
| IV | 12 bytes aleatorios por operación |
| Etiqueta de autenticación | 128 bits |
| Generador aleatorio | `SecureRandom` |
| Codificación binaria | Base64 URL-safe sin padding |

La representación almacenada tiene esta estructura:

```text
EH2(salt.iv.ciphertextConEtiqueta)
```

Cada componente se codifica por separado. El último contiene la salida de AES-GCM, incluida la etiqueta de autenticación. La clave no se incluye en el texto cifrado.

El salt y el IV aleatorios hacen que el mismo texto cifrado varias veces con la misma clave produzca resultados diferentes. Conserve la cadena completa al almacenarla: truncarla o alterar sus componentes puede impedir el descifrado.

Los algoritmos y el número de iteraciones están fijados en la implementación asociada a `EH2`; no son parámetros configurables de la API ni campos independientes del mensaje.

## Compatibilidad y migración

Desde la versión 7.0.0:

- Los nuevos cifrados utilizan `EH2(...)`.
- Se elimina `getEncryptKey()` y se incorpora `hasEncryptKeyConfigured()`.
- Los textos planos vacíos y blancos pueden cifrarse.
- Se mantiene el descifrado de datos legados mediante Jasypt `BasicTextEncryptor`.

La lectura de un dato legado no cambia su formato almacenado. Para migrarlo, la aplicación debe descifrarlo con la clave original y volver a cifrarlo con la versión actual:

```java
EncryptorService servicio = new EncryptorServiceImpl();

String textoPlano = servicio.decrypt(cifradoAnterior, claveAnterior);
String cifradoActual = servicio.encrypt(textoPlano, claveActual);
```

Verifique la recuperación del nuevo valor antes de sustituir el anterior. La aplicación gestiona la persistencia y la selección de claves: el formato no incorpora un identificador de clave y `setEncryptKey` no vuelve a cifrar los datos existentes.

## Gestión de claves y registro de actividad

Obtenga las claves desde la configuración protegida o el gestor de secretos de la aplicación. Evite incorporarlas al código fuente o a ejemplos de producción, y mantenga fuera de los logs las claves y los textos en claro.

La API utiliza `String` para recibir las claves y conservar la clave por defecto. No expone una operación de borrado seguro de esa memoria. Si comparte el servicio entre hilos, configure su clave antes de utilizarlo y evite modificarla durante las operaciones; la clase no declara un contrato de sincronización para esos cambios.

El registro se realiza mediante SLF4J. La biblioteca registra mensajes de éxito a nivel `INFO` y detalles técnicos de los errores a nivel `DEBUG`, sin incluir explícitamente las claves ni los textos procesados en sus mensajes. La aplicación consumidora debe proporcionar su implementación de logging y ajustar los niveles según el volumen de operaciones.

## Dependencias

Versiones gestionadas por el parent `jarios-parent:1.0.15` para las dependencias declaradas en este proyecto:

| Dependencia | Versión | Ámbito | Finalidad |
|---|---|---|---|
| `org.slf4j:slf4j-api` | 2.0.20 | `compile` | API de logging. |
| `org.jasypt:jasypt` | 1.9.3 | `compile` | Descifrado del formato legado. |
| `ch.qos.logback:logback-classic` | 1.6.4 | `test` | Logging durante las pruebas. |
| `org.junit.jupiter:junit-jupiter` | 6.1.3 | `test` | Ejecución de pruebas unitarias. |
| `org.assertj:assertj-core` | 3.27.7 | `test` | Aserciones de las pruebas. |

El cifrado `EH2` utiliza las API criptográficas del JDK. Logback y las bibliotecas de pruebas no se imponen como dependencias de ejecución de los consumidores.

## Construcción y verificación

Desde la raíz del repositorio, con `JAVA_HOME` apuntando al JDK:

| Operación | Windows | Linux y macOS |
|---|---|---|
| Comprobar Maven y Java | `.\mvnw.cmd -version` | `./mvnw -version` |
| Ejecutar pruebas | `.\mvnw.cmd test` | `./mvnw test` |
| Construir y verificar | `.\mvnw.cmd clean verify` | `./mvnw clean verify` |
| Verificar calidad | `.\mvnw.cmd -Pquality verify` | `./mvnw -Pquality verify` |
| Instalar en el repositorio Maven local | `.\mvnw.cmd install` | `./mvnw install` |

En Linux y macOS puede ser necesario ejecutar `chmod +x mvnw`. El primer uso del Wrapper puede descargar Maven y las dependencias.

El POM referencia `../jarios-parent/pom.xml`, pero el parent debe corresponder a la versión declarada, **1.0.15**. Si el proyecto vecino contiene otra versión, Maven debe resolver la versión requerida desde el repositorio local o remoto.

El proceso de construcción genera en `target/` el JAR de la biblioteca y los artefactos de fuentes y Javadoc:

```text
encrypt-helper-7.1.0.jar
encrypt-helper-7.1.0-sources.jar
encrypt-helper-7.1.0-javadoc.jar
```

Las pruebas incluidas cubren el cifrado y descifrado con ambas modalidades de clave, entradas vacías y nulas, rechazo de claves inválidas, fallo con clave incorrecta y lectura de cifrados legados, además de las utilidades de validación de cadenas.

### Herramientas de construcción y calidad

| Herramienta | Versión configurada | Uso |
|---|---|---|
| Maven Enforcer Plugin | 3.6.3 | Requisitos de Java y Maven. |
| Maven Compiler Plugin | 3.16.0 | Compilación para Java 21. |
| Maven Surefire Plugin | 3.6.0 | Pruebas unitarias. |
| Maven Clean Plugin | 3.5.0 | Limpieza de la construcción. |
| Maven JAR Plugin | 3.5.1 | Empaquetado de la biblioteca. |
| Maven Source Plugin | 3.4.0 | Artefacto de fuentes. |
| Maven Javadoc Plugin | 3.12.0 | Artefacto Javadoc. |
| Maven Dependency Plugin | 3.11.0 | Inspección de dependencias. |
| Versions Maven Plugin | 2.22.0 | Revisión de versiones. |
| Spotless Maven Plugin | 3.10.3 | Comprobación de formato en `quality`. |
| Google Java Format | 1.36.1 | Formateador utilizado por Spotless. |
| Maven Checkstyle Plugin | 3.6.0 | Reglas de estilo en `quality`. |
| Checkstyle | 14.1.0 | Motor de reglas `google_checks.xml`. |
| SpotBugs Maven Plugin | 4.10.4.1 | Análisis estático en `quality`. |
| OWASP Dependency-Check | 13.0.0 | Análisis de dependencias mediante invocación explícita. |

La tabla recoge las herramientas relevantes de este proyecto con versiones fijadas en el parent. El perfil `quality` comprueba formato, estilo y análisis estático; Checkstyle está configurado para bloquear la construcción ante infracciones. OWASP Dependency-Check se ejecuta por separado de `verify`.

## Automatización y publicación

| Workflow | Activación configurada | Función |
|---|---|---|
| CI | Push y PR sobre `main`; ejecución manual | `clean verify`. |
| Quality | Push y PR sobre `main`; ejecución manual | `-Pquality verify`. |
| Dependency Check | Lunes a las 04:10 UTC; ejecución manual | Análisis OWASP y conservación del informe generado. |
| Release Package | Tags `v*`; ejecución manual con `release_tag` | Verificación, publicación Maven y creación de release con JAR adjuntos. |

El workflow de publicación exige que la versión del tag coincida con el POM. Para publicar esta versión, utilice el tag `v7.1.0` y asegúrese de que el parent `1.0.15` está disponible para los consumidores. Publicar requiere acceso de escritura a los paquetes y al repositorio.

Los workflows emplean `PACKAGES_TOKEN` o, como alternativa configurada, `GITHUB_TOKEN` para leer paquetes; este último debe disponer de acceso a los paquetes necesarios. El análisis OWASP toma `NVD_API_KEY` de los secretos configurados.

### Actualizaciones con Dependabot

El repositorio incluye `.github/dependabot.yml` con revisiones semanales en la zona horaria `Europe/Madrid`:

- **Maven:** lunes a las 06:00, con actualizaciones menores y de parche agrupadas.
- **GitHub Actions:** lunes a las 06:30, con las acciones agrupadas.
- Destino `main` y límite de cinco PR abiertas por ecosistema.

La configuración no define fusión automática. Las versiones de las dependencias heredadas se mantienen en `jarios-parent`; los cambios deben coordinarse con ese proyecto y verificarse en esta biblioteca.

El archivo actual de Dependabot no declara registros Maven privados. Si necesita consultar GitHub Packages, debe configurarse ese acceso en Dependabot; la configuración de credenciales de los workflows no sustituye la del servicio de actualizaciones.

## Resolución de problemas

| Síntoma | Comprobaciones |
|---|---|
| No se resuelve la biblioteca o el parent | Versión disponible, repositorios, credenciales e identificadores de servidor. |
| Error 401 o 403 al descargar paquetes | Acceso del usuario o token al paquete solicitado. |
| Maven rechaza Java o su propia versión | `JAVA_HOME` y salida de `mvnw -version`. |
| Clave por defecto no configurada | Constructor con clave o llamada previa a `setEncryptKey`. |
| Fallo de autenticación al descifrar | Clave original y conservación íntegra del texto cifrado. |
| Un texto legado no se recupera | Clave original y compatibilidad con `BasicTextEncryptor`. |
| Fallo del perfil `quality` | Informes de formato, Checkstyle y SpotBugs. |
| No se publica una versión | Coincidencia entre tag y POM, validaciones y permisos. |

## Documentación y mantenimiento

- [Historial de cambios](CHANGELOG.md).
- [Auditoría viva de agosto de 2026](docs/auditorias/auditoria-viva-2026-08-12.md).
- [Decisión criptográfica de agosto de 2026](docs/auditorias/decision-criptografia-2026-08-12.md).
- [Auditoría inicial de mayo de 2026](doc/auditoria/2026_05_09/auditoria-proyecto.md).

Mantenga este README y el historial de cambios sincronizados con el POM, la API y los workflows. Las notas de la versión 7.1.0 describen las actualizaciones de herramientas y documentación.

## Licencia

El repositorio no incluye un archivo de licencia propio ni una declaración de licencia en el POM del proyecto o en el parent revisado. Las dependencias conservan sus respectivas licencias.
