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

## Instalación y uso

### Dependencia Maven

Agrega esta dependencia en tu `pom.xml`:

```xml
<dependency>
    <groupId>local.jarios</groupId>
    <artifactId>encryptor</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Ejemplo de uso de Encryptor en Java

```java
import local.jarios.encryptor.config.Encryptor;

public class EjemploEncryptor {
    public static void main(String[] args) {
        String claveMaestra = "MiClaveSecreta123";
        String textoPlano = "contraseña_super_secreta";

        // Encriptar
        String cifrado = Encryptor.encrypt(textoPlano, claveMaestra);
        System.out.println("Texto cifrado: " + cifrado);

        // Desencriptar
        String descifrado = Encryptor.decrypt(cifrado, claveMaestra);
        System.out.println("Texto descifrado: " + descifrado);
    }
}
```

# Salida ejemplo

```bash
java -jar encryptor.jar MiClaveSecreta123 contraseñaSuperSecreta
```

```yml
Texto cifrado: 1xKmRcvFmMDSD9uZwq+qfw==
Texto descifrado: contraseña_super_secreta
```