# Auditoria del proyecto encrypt-helper

Fecha: 2026-05-09  
Ruta auditada: `C:\java\desarrollo\encrypt-helper`  
Version declarada: `local.jarios:encrypt-helper:5.2.0`

## Resumen ejecutivo

El proyecto es una libreria Java/Maven pequena que expone un servicio de cifrado y descifrado de texto mediante Jasypt `BasicTextEncryptor`. La superficie funcional es reducida, pero el objetivo del proyecto es sensible porque trata claves y secretos. El estado actual no deberia considerarse listo para uso productivo sin correcciones, principalmente por una clave por defecto hardcodeada, documentacion desalineada, ausencia de pruebas unitarias reales, falta de verificacion CI y uso de primitives criptograficas antiguas de Jasypt.

Riesgo global estimado: **Alto** para uso con secretos reales. **Medio** si se usa solo como utilidad interna controlada y con claves externas robustas.

## Alcance y metodologia

Se revisaron:

- Estructura del repositorio y estado Git.
- `pom.xml`, dependencias, plugins y perfiles Maven.
- Codigo fuente bajo `src/main/java`.
- Codigo bajo `src/test/java` y recursos de test.
- `README.md`.
- Workflow GitHub Actions.
- Artefacto existente `target/encrypt-helper-5.2.0.jar`.
- Busqueda de patrones sensibles: claves, passwords, logs, `System.exit`, `TODO/FIXME`.

Limitaciones:

- No se pudo ejecutar `mvn test` ni `mvn dependency:tree` porque `mvn` no esta disponible en el `PATH` de la sesion.
- No se pudo usar `jar` porque tampoco esta disponible en el `PATH`; el JAR se inspecciono como ZIP.
- No hay wrapper Maven (`mvnw`) en el repositorio.
- No hay informes Surefire previos en `target/surefire-reports`.

## Estado del repositorio

Al iniciar la auditoria habia cambios sin confirmar:

- `pom.xml` modificado.
- `src/main/resources/spotbugs-security-exclude.xml` eliminado.
- `src/main/resources/spotbugs-security-include.xml` eliminado.
- `src/main/java/local/jarios/encrypt/EncryptDemo.java` movido a `src/test/java/local/jarios/encrypt/EncryptDemo.java`.
- `src/main/resources/logback.xml` movido a `src/test/resources/logback-test.xml`.

Estos cambios forman parte del estado auditado y no se han revertido.

## Inventario tecnico

- Lenguaje: Java.
- Build: Maven.
- Version Java requerida por Maven: 21 (`pom.xml:34-35`).
- Version Java indicada en README: Java 8 o superior (`README.md:18-19`).
- Dependencias principales:
  - `org.jasypt:jasypt:1.9.3`.
  - `ch.qos.logback:logback-classic:1.5.24`.
  - `org.slf4j:slf4j-api:2.0.17`.
  - `org.projectlombok:lombok:1.18.42` con scope `provided`.
  - JUnit 5.8.2 y AssertJ 3.21.0 en scope `test`.
- Perfil `quality` con SpotBugs y Checkstyle, pero sin configuracion de reglas ni ejecuciones asociadas al ciclo normal.
- Workflow GitHub Actions solo crea releases sobre tags; no compila, no testea y no publica artefactos Maven.

## Hallazgos criticos y altos

### 1. Clave por defecto hardcodeada

Se define una clave fija en `src/main/java/local/jarios/encrypt/common/util/Constantes.java:17`:

```java
public static final String DEFAULT_SECRET_KEY = "Malaga$$2025";
```

Impacto:

- Cualquier dato cifrado con la clave por defecto queda comprometido si el codigo o artefacto se distribuye.
- La clave queda incluida en el JAR compilado.
- El constructor de `EncryptorServiceImpl` usa esa clave automaticamente (`EncryptorServiceImpl.java:32-33`), por lo que el uso inseguro es el camino por defecto.

Recomendacion:

- Eliminar la clave real del codigo.
- Obligar a inyectar la clave por constructor, variable de entorno, proveedor externo o configuracion segura.
- Si se mantiene un valor por defecto para tests, que sea solo en tests y con nombre explicito de no-produccion.

Prioridad: **Critica**.

### 2. Algoritmo criptografico poco explicito y potencialmente obsoleto

`EncryptorServiceImpl` usa `BasicTextEncryptor` (`EncryptorServiceImpl.java:88-91` y `138-141`). En Jasypt 1.9.3, esta clase abstrae el algoritmo y no expone en la API del proyecto parametros como algoritmo, iteraciones, salt generator o provider.

Impacto:

- Dificulta demostrar cumplimiento criptografico.
- No permite evolucionar parametros sin cambiar implementacion.
- Puede no cumplir expectativas modernas para almacenamiento de secretos si se compara con PBKDF2/Argon2/AES-GCM o servicios KMS.

Recomendacion:

- Documentar explicitamente algoritmo, modo, derivacion de clave, salt e iteraciones.
- Valorar migrar a `StandardPBEStringEncryptor` con parametros configurables o, preferiblemente, una implementacion moderna con AEAD si el caso de uso lo requiere.
- Definir versionado de formato para poder migrar textos cifrados.

Prioridad: **Alta**.

### 3. Logging de informacion sensible

Hay logs que exponen valores potencialmente sensibles:

- `StringHelper.java:42` registra el contenido de cualquier cadena validada.
- `EncryptDemo.java` registra clave por defecto, textos originales, cifrados y descifrados.
- `README.md:96-97` muestra en el ejemplo logging de clave y texto.

Impacto:

- Posible fuga de secretos en logs locales, CI o sistemas centralizados.
- Las cadenas validadas pueden incluir claves, tokens o textos planos.

Recomendacion:

- No registrar claves, textos planos ni textos descifrados.
- Sustituir logs por mensajes sin valores o con mascarado controlado.
- Evitar helpers genericos que logueen argumentos.

Prioridad: **Alta**.

### 4. Ausencia de pruebas unitarias reales

El repositorio tiene dependencias JUnit, pero `rg -n "@Test|assert" src\test src\main` no encontro pruebas. Solo existe `src/test/java/local/jarios/encrypt/EncryptDemo.java`, que es una demo con `main`.

Impacto:

- No hay garantia automatizada de cifrado/descifrado, validaciones, errores o compatibilidad.
- Cambios en dependencias o refactors pueden romper el contrato sin deteccion.

Recomendacion:

- Crear tests para:
  - Cifrar y descifrar con clave explicita.
  - Cifrar y descifrar con clave configurada.
  - Rechazo de `null`, blanco y clave invalida.
  - Error al descifrar con clave incorrecta.
  - No determinismo esperado del cifrado si hay salt.
- Ejecutar tests en CI.

Prioridad: **Alta**.

## Hallazgos medios

### 5. README desactualizado respecto al codigo real

Ejemplos:

- Declara Java 8 o superior, pero `pom.xml` exige Java 21 (`README.md:18-19`, `pom.xml:34-35`).
- Declara dependencia `artifactId` `encryptorService` version `1.0.3`, pero el proyecto actual es `encrypt-helper` version `5.2.0` (`README.md:33-37`, `pom.xml:4-6`).
- Usa clases/metodos inexistentes en el estado actual: `Encryptor`, `EncryptorImpl`, `setDefaultKey`, `getDefaultKey`, `encrypt(textoPlano)` y `decrypt(textoCifrado)`.
- Afirma "Test unitarios con JUnit 5" (`README.md:14`) pero no hay tests unitarios.

Impacto:

- Integradores no podran usar la libreria copiando la documentacion.
- Riesgo de publicar una API distinta a la documentada.

Recomendacion:

- Actualizar README a `EncryptorService` / `EncryptorServiceImpl`, `setEncryptKey`, `getEncryptKey`, `encryptDefaultKey`, `decryptDefaultKey`.
- Corregir coordenadas Maven y requisitos Java.
- Retirar afirmaciones de pruebas hasta que existan.

Prioridad: **Media**.

### 6. CI/CD insuficiente

`.github/workflows/release.yml` solo crea un release con `ncipollo/release-action@v1` cuando se sube un tag (`release.yml:3-20`). No hay pasos de:

- `actions/setup-java`.
- Cache Maven.
- `mvn test`.
- `mvn -Pquality verify`.
- Publicacion de paquete Maven.
- Adjuntar JAR/source/javadoc al release.

Impacto:

- Se pueden crear releases sin compilar ni testear.
- El artefacto publicado podria no corresponder al codigo del tag.

Recomendacion:

- Anadir workflow de pull request y push con build/test.
- En release, construir desde cero y publicar artefactos.
- Actualizar `actions/checkout@v3` a una version soportada actual en el momento de mantenimiento.

Prioridad: **Media**.

### 7. Perfil de calidad no integrado

El perfil `quality` declara SpotBugs y Checkstyle (`pom.xml:175-195`), pero:

- No hay configuracion Checkstyle visible.
- Los ficheros `spotbugs-security-include.xml` y `spotbugs-security-exclude.xml` aparecen eliminados.
- No se enlaza a fases del build normal.

Impacto:

- El perfil puede ejecutarse con reglas por defecto o no aportar controles esperados.
- No hay evidencia de analisis estatico reproducible.

Recomendacion:

- Restaurar o sustituir configuracion de SpotBugs/FindSecBugs si se pretende auditoria de seguridad.
- Enlazar `check`/`spotbugs` a `verify` en CI.
- Documentar `mvn -Pquality verify`.

Prioridad: **Media**.

### 8. Dependencias de logging como transitivas de libreria

La libreria incluye `logback-classic` como dependencia normal (`pom.xml:66-71`). Para una libreria, esto fuerza una implementacion de logging en consumidores.

Impacto:

- Puede provocar conflictos con aplicaciones que ya usan otra implementacion SLF4J.
- Aumenta el acoplamiento operativo.

Recomendacion:

- Mantener `slf4j-api` como dependencia.
- Mover `logback-classic` a scope `test` o a ejemplos/demos.
- Dejar que la aplicacion consumidora decida la implementacion de logging.

Prioridad: **Media**.

### 9. Utilidad con `System.exit` dentro de codigo principal

`FinalDelProgramaHelper.finalizar` llama a `System.exit` (`FinalDelProgramaHelper.java:49`) y esta clase esta en `src/main/java`.

Impacto:

- Si un consumidor llama accidentalmente esta utilidad desde una aplicacion embebida, puede terminar el proceso completo.
- Es dificil de testear y no encaja bien en una libreria.

Recomendacion:

- Mover helpers de CLI/demo fuera de `src/main`.
- Preferir devolver codigos de salida al launcher, no llamar `System.exit` desde utilidades compartidas.

Prioridad: **Media**.

## Hallazgos bajos

### 10. Nombres y comentarios con errores o inconsistencias

Ejemplos:

- `README.md:40`: "instanacia".
- `EncryptorServiceImpl.java:87`: "encritptacion".
- Comentarios del README hablan de Base64, aunque el codigo delega directamente en Jasypt y no realiza codificacion explicita propia.

Impacto:

- Baja confianza documental.
- Puede confundir al usuario sobre el formato real del cifrado.

Recomendacion:

- Revisar documentacion y JavaDoc junto con la API publica.

Prioridad: **Baja**.

### 11. Paquetes `local.*`

El `groupId` y paquetes usan `local.jarios`. Si la libreria se publica en GitHub Packages y se consume por terceros, conviene usar un namespace estable ligado a la organizacion o dominio.

Impacto:

- Menor trazabilidad y posible incoherencia con repositorio `contratacionmalaga/encrypt_helper`.

Recomendacion:

- Valorar migracion de group/package en una version mayor si la libreria va a publicarse formalmente.

Prioridad: **Baja**.

## Observaciones sobre artefacto existente

Existe `target/encrypt-helper-5.2.0.jar` con clases compiladas de la libreria:

- `EncryptorService.class`.
- `EncryptorServiceImpl.class`.
- `Constantes.class`.
- `Mensajes.class`.
- `TipoFinalEjecucion.class`.
- `EncryptorException.class`.
- `FinalDelProgramaHelper.class`.
- `StringHelper.class`.

No incluye la demo movida a `src/test`, lo cual es correcto para el artefacto principal. Sin embargo, al contener `Constantes.class`, tambien contiene la clave por defecto hardcodeada.

## Recomendaciones priorizadas

### Corto plazo

1. Eliminar `DEFAULT_SECRET_KEY` real y exigir clave externa.
2. Retirar logs de claves, textos planos y textos descifrados.
3. Actualizar README para que compile con la API actual.
4. Anadir tests unitarios minimos de contrato.
5. Anadir Maven Wrapper para builds reproducibles.

### Medio plazo

1. Revisar estrategia criptografica y documentar parametros.
2. Sustituir o encapsular Jasypt con formato versionado.
3. Mover `logback-classic` a `test` y dejar solo `slf4j-api` para consumidores.
4. Integrar `mvn test` y `mvn -Pquality verify` en GitHub Actions.
5. Restaurar reglas SpotBugs/FindSecBugs o una alternativa equivalente.

### Largo plazo

1. Definir politica de versionado semantico y compatibilidad de API.
2. Publicar artefactos con source/javadoc y release notes generadas desde CI.
3. Considerar namespace organizativo estable.
4. Documentar modelo de amenazas: que protege la libreria, contra quien y que queda fuera de alcance.

## Criterios de aceptacion propuestos

Antes de considerar la libreria lista para uso con secretos reales:

- No existe ninguna clave real o utilizable en `src/main`.
- Ningun log imprime claves, textos planos ni descifrados.
- `mvn test` se ejecuta correctamente en entorno limpio.
- CI bloquea merges/releases si fallan tests o calidad.
- README contiene ejemplos que compilan con la API publicada.
- La estrategia criptografica queda documentada y tiene plan de migracion.

## Conclusion

La base del proyecto es simple y mantenible por tamano, pero el contexto de seguridad exige subir el nivel de controles. El mayor riesgo no esta en la complejidad del codigo, sino en decisiones por defecto: clave embebida, logging sensible, falta de tests y releases sin build verificable. Corregir esos puntos deberia ser prioritario antes de ampliar funcionalidad o publicar nuevas versiones.
