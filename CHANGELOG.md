# Changelog

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
