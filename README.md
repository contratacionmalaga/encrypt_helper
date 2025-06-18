# Encryptor

Servicio Java para encriptar y desencriptar claves usando una clave maestra, pensado para almacenar valores seguros en ficheros `.properties` o cualquier otro sistema que requiera almacenamiento seguro de texto.

---

## Características principales

- **Encriptación** y **desencriptación** de texto con contraseña (clave maestra).
- Utiliza [`org.jasypt.util.text.BasicTextEncryptor`](https://www.jasypt.org/api/jasypt/1.9.3/org/jasypt/util/text/BasicTextEncryptor.html) para cifrado.
- Los textos cifrados se codifican en Base64 para fácil almacenamiento.
- Excepciones personalizadas para errores de cifrado (`EncryptorException`).
- Logging con [SLF4J](https://www.slf4j.org/) para seguimiento y auditoría.
- Test unitarios con JUnit 5 para garantizar la calidad del código.
- Fácil integración en otros proyectos como dependencia Maven.
- Preparado para publicación en GitHub Packages.
---
# Requisitos para su funcionamiento
- Java 8 o superior
- Biblioteca Jasypt (dependencia Maven)
- Biblioteca Lombok (para anotaciones @Slf4j, @Getter)
````
<dependency>
    <groupId>org.jasypt</groupId>
    <artifactId>jasypt</artifactId>
    <version>1.9.3</version>
</dependency>
````
--
### Dependencia Maven
Agrega esta dependencia en tu `pom.xml`:
```xml
<dependency>
    <groupId>local.jarios</groupId>
    <artifactId>encryptorService</artifactId>
    <version>1.0.3</version>
</dependency>
```
--
# Crear una instanacia del servicio

```
Encryptor encryptorService = new EncryptorImpl();
```
--
# Configurar la clave por defecto

```
encryptorService.setDefaultKey("MiClaveSegura2025");
```
--
# Cifrar texto con clave por defecto

```
String textoPlano = "Mi secreto";
String textoCifrado = encryptorService.encrypt(textoPlano);
```
--
# Cifrar texto con clave personalizada
```
String clavePersonalizada = "ClaveDiferente!";
String textoCifrado = encryptorService.encrypt(textoPlano, clavePersonalizada);
```
--
# Descifrar texto con clave por defecto
```
String textoDescifrado = encryptorService.decrypt(textoCifrado);
```
--
# Descifrar texto con clave personalizada
```
String textoDescifrado = encryptorService.decrypt(textoCifrado, clavePersonalizada);
```
--
# Ejemplo completo
````
    public static void main(String[] args) {

        Encryptor encryptorService = new EncryptorImpl();
        log.info("Servicio Encryptor creado correctamente.");

        // Muestra clave por defecto inicial
        log.info("Clave por defecto inicial: {}", encryptorService.getDefaultKey());

        String claveMaestra;
        String textoOriginal;

        if (args.length == 2) {
            claveMaestra = args[0];
            textoOriginal = args[1];
            log.info("Parámetros recibidos: clave maestra oculta, texto original: {}", textoOriginal);
        } else {
            claveMaestra = encryptorService.getDefaultKey();
            textoOriginal = TEXTO_ORIGINAL_DEFAULT;
            log.info("No se recibieron parámetros válidos. Usando valores por defecto.");
            log.info("Clave maestra por defecto: {}", claveMaestra);
            log.info("Texto original por defecto: {}", textoOriginal);
        }

        try {
            // Cambiamos la clave por defecto a la clave recibida (o la que ya tenía)
            encryptorService.setDefaultKey(claveMaestra);
            log.info("Clave por defecto configurada a: {}", encryptorService.getDefaultKey());

            // CIFRADO usando clave por defecto (sin pasar la clave explícita)
            String cifradoConClaveDefecto = encryptorService.encrypt(textoOriginal);
            log.info("Texto cifrado usando clave por defecto: {}", cifradoConClaveDefecto);

            // CIFRADO usando clave personalizada (pasándola explícitamente)
            String cifradoConClavePersonalizada = encryptorService.encrypt(textoOriginal, claveMaestra);
            log.info("Texto cifrado usando clave personalizada: {}", cifradoConClavePersonalizada);

            // DESCIFRADO usando clave por defecto
            String descifradoConClaveDefecto = encryptorService.decrypt(cifradoConClaveDefecto);
            log.info("Texto descifrado usando clave por defecto: {}", descifradoConClaveDefecto);

            // DESCIFRADO usando clave personalizada
            String descifradoConClavePersonalizada = encryptorService.decrypt(cifradoConClavePersonalizada, claveMaestra);
            log.info("Texto descifrado usando clave personalizada: {}", descifradoConClavePersonalizada);

            // Prueba de envoltorio "ENC(...)"
            String envoltorio = String.format("ENC(%s)", cifradoConClaveDefecto);
            log.info("Texto cifrado con envoltorio: {}", envoltorio);

            // Extraemos el texto cifrado del envoltorio y lo desciframos
            String cifradoSinEnvoltorio = envoltorio.substring(4, envoltorio.length() - 1);
            String descifradoDesdeEnvoltorio = encryptorService.decrypt(cifradoSinEnvoltorio);
            log.info("Texto descifrado desde envoltorio usando clave por defecto: {}", descifradoDesdeEnvoltorio);

        } catch (Exception e) {
            log.error("Error en cifrado/descifrado", e);
        }
    }
````