# Changelog

## 7.0.1 - Release definitiva GitHub 2026-08-12

### CI/CD

- Migradas las acciones oficiales de GitHub a majors compatibles con Node 24: `actions/checkout@v7` y `actions/setup-java@v5`.
- Eliminados avisos no bloqueantes de runtime Node 20 y deprecacion de `setup-java@v4`.
- Podadas ramas de trabajo ya mergeadas para dejar `main` como unica rama activa del repositorio.

### Documentacion

- Auditoria viva actualizada con el hito `AV-13`.
- Corregida la tabla de evidencias de la auditoria viva.

### Compatibilidad

- Sin cambios de API ni de formato respecto a `7.0.0`.
## 7.0.0 - Aplicacion de auditoria viva 2026-08-12

### Seguridad

- Sustituido el cifrado nuevo basado en Jasypt `BasicTextEncryptor` por un formato versionado `EH2(...)` con AES-GCM y PBKDF2-HMAC-SHA256.
- Anadida autenticacion criptografica: una clave incorrecta o un texto manipulado falla de forma controlada.
- Mantenida compatibilidad de lectura para textos cifrados antiguos sin prefijo `EH2(` mediante descifrado legado Jasypt.
- Eliminado `getEncryptKey()` de la API publica para no exponer claves en claro.
- Anadido `hasEncryptKeyConfigured()` como alternativa segura.
- Separada la validacion de claves y payloads: los textos vacios o blancos pueden cifrarse; las claves blancas siguen rechazandose.

### Calidad

- Retirado Lombok del codigo principal y de la demo de test.
- Actualizadas dependencias de test y plugins Maven estables.
- Checkstyle pasa a ser bloqueante con `failOnViolation=true`.
- Simplificado CI para ejecutar un unico build con el perfil `quality`.

### Plataforma

- Subida la version del proyecto a `7.0.0` por cambios incompatibles de API y formato.
- Actualizado Maven Wrapper a Maven 3.9.16.
- Documentada la recomendacion de usar el ultimo parche de Java 21 LTS.

### Compatibilidad

- Cambio incompatible: desaparece `EncryptorService#getEncryptKey()`.
- Cambio de formato: los cifrados nuevos se generan como `EH2(...)`.
- Compatibilidad mantenida: `decrypt` sigue aceptando textos legados de Jasypt sin prefijo.

## 6.0.0 - Actuacion de auditoria 2026-05-09

### Seguridad

- Eliminada la clave de cifrado hardcodeada de `src/main`.
- `EncryptorServiceImpl` ya no configura una clave por defecto implicita.
- Los metodos `encryptDefaultKey` y `decryptDefaultKey` fallan de forma controlada si no hay clave configurada.
- Retirado logging de valores sensibles en validaciones y demo.
- Eliminadas utilidades de finalizacion con `System.exit` del artefacto principal.

### Calidad

- Anadidos tests unitarios para cifrado, descifrado, validaciones y operaciones con clave configurada.
- Configurado Surefire para ejecutar JUnit 5.
- Activado perfil `quality` con SpotBugs y Checkstyle.
- `logback-classic` pasa a scope `test` para no imponer implementacion de logging a consumidores.

### CI/CD

- Anadido workflow de CI para build, tests y perfil de calidad.
- Endurecido workflow de release para compilar y testear antes de crear la release.
- Automatizada la publicacion del paquete Maven en GitHub Packages al publicar tags o releases.
- Generados artefactos principal, sources y javadoc para asociarlos a cada release.

### Documentacion

- README actualizado con API real, Java 21, coordenadas Maven actuales y recomendaciones de seguridad.
- Anadida decision criptografica para documentar el mantenimiento temporal de Jasypt `BasicTextEncryptor`.

### Compatibilidad

- Cambio potencialmente incompatible: desaparece la clave por defecto hardcodeada. Las aplicaciones consumidoras deben proporcionar la clave explicitamente.
