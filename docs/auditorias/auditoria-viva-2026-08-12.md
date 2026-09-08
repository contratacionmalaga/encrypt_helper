# Auditoria viva del proyecto encrypt-helper

Fecha de creacion: 2026-08-12
Ultima revision: 2026-09-08
Proyecto: `local.jarios:encrypt-helper`
Version auditada inicialmente: `6.0.0`
Version definitiva preparada: `7.0.1`
Rama principal activa: `main`
Commit base inicial: `6c5e47b Subir version a 6.0.0`

## Como mantener viva esta auditoria

Este documento es el punto de seguimiento. Cada vez que se cierre un hito:

1. Cambiar el estado en la matriz de hitos.
2. Anadir fecha de cierre, PR/commit o comando que lo evidencie.
3. Actualizar el resumen ejecutivo si cambia el riesgo global.
4. Mover hallazgos cerrados a "Historial de hitos cerrados" si la tabla crece demasiado.
5. Ejecutar de nuevo las comprobaciones minimas y actualizar la seccion "Evidencias".

Estados permitidos: `Pendiente`, `En curso`, `Bloqueado`, `Hecho`, `Aceptado con deuda`.

## Resumen ejecutivo actualizado

La auditoria viva se aplico inicialmente sobre ramas `jarp/*` y queda consolidada en `main` como version definitiva `7.0.1`. La version mayor `7.0.0` introdujo cambios incompatibles de API y formato criptografico; `7.0.1` consolida CI/CD, limpieza de ramas y publicacion definitiva en GitHub.

El riesgo global baja de **Medio/Alto** a **Medio-bajo** para uso interno controlado. Los nuevos cifrados usan formato versionado `EH2(...)` con AES-GCM y PBKDF2-HMAC-SHA256, y el descifrado mantiene compatibilidad con textos Jasypt legados sin prefijo. La API ya no expone la clave configurada.

El riesgo residual principal queda en la dependencia de lectura legada con Jasypt y en la actualizacion del JDK instalado localmente, que es una accion externa al repositorio. La estructura de ramas queda profesionalizada dejando `main` como unica rama activa y usando tags para releases.

## Evidencias verificadas

| Comprobacion | Resultado | Fecha | Evidencia |
| --- | --- | --- | --- |
| Rama de trabajo | Correcto | 2026-08-12 | `git branch --show-current`: `jarp/aplicar-auditoria-viva` |
| Build de tests | Correcto | 2026-08-12 | `.\mvnw.cmd -B test`: 14 tests, 0 fallos, 0 errores |
| Perfil de calidad | Correcto | 2026-08-12 | `.\mvnw.cmd -B -Pquality verify`: build success, SpotBugs 0 issues, Checkstyle 0 violations |
| Formato criptografico nuevo | Correcto | 2026-08-12 | Tests validan prefijo `EH2(` y fallo con clave incorrecta |
| Compatibilidad legado | Correcto | 2026-08-12 | Test `decryptsLegacyJasyptTextWithoutVersionPrefix` |
| API sin exposicion de clave | Correcto | 2026-08-12 | `getEncryptKey()` eliminado, `hasEncryptKeyConfigured()` anadido |
| Maven Wrapper actualizado | Correcto | 2026-08-12 | `.\mvnw.cmd -version`: Apache Maven 3.9.16 |
| Checkstyle bloqueante | Correcto | 2026-08-12 | `failOnViolation=true` y quality verde |
| Dependencias estables | Correcto | 2026-08-12 | POM actualizado; solo quedan disponibles `slf4j-api` alpha, Surefire milestone y betas Maven 4, descartadas para release estable |

## Inventario tecnico actualizado

| Elemento | Estado actualizado |
| --- | --- |
| Java objetivo | 21 (`maven.compiler.source` y `target`) |
| Java local observado antes de cambios | 21.0.9, Oracle Corporation |
| JDK recomendado | Ultimo parche disponible de Java 21 LTS |
| Maven Wrapper | 3.9.16 |
| Maven minimo exigido | 3.6.3 |
| Artefacto | JAR |
| Formato nuevo | `EH2(...)` |
| Cifrado nuevo | AES-GCM con tag de 128 bits |
| Derivacion de clave | PBKDF2-HMAC-SHA256, 210.000 iteraciones, salt de 16 bytes |
| Compatibilidad legado | Descifrado Jasypt para textos sin prefijo `EH2(` |
| Logging API | `org.slf4j:slf4j-api:2.0.17` en compile |
| Logging runtime | `logback-classic:1.6.2` en test |
| Tests | JUnit Jupiter 5.14.4 + AssertJ 3.27.7 |
| Calidad | SpotBugs 4.10.3.0 + Checkstyle bloqueante |
| Lombok | Retirado |
| CI | Unico build `./mvnw -B -Pquality clean verify` |
| Release | `./mvnw -B -Pquality clean deploy` antes de publicar artefactos |
| GitHub Actions runtime | `actions/checkout@v7` y `actions/setup-java@v5`, compatibles con Node 24 |
| Ramas activas en GitHub | Solo `main`; ramas de trabajo mergeadas eliminadas |
| Dependabot Updates | Configurado para Maven y GitHub Actions con revision semanal |

## Hallazgos actualizados

### H01 - Estrategia criptografica no apta para alto impacto

Prioridad: **P0**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Se implementa formato `EH2(...)` con AES-GCM y PBKDF2-HMAC-SHA256. La decision queda documentada en `docs/auditorias/decision-criptografia-2026-08-12.md`.

Riesgo residual: Jasypt sigue presente solo para descifrado legado. Debe retirarse en una version mayor futura cuando no queden datos antiguos.

### H02 - La API publica expone la clave configurada

Prioridad: **P1**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Se elimina `EncryptorService#getEncryptKey()` y se anade `hasEncryptKeyConfigured()`.

### H03 - Validacion de entrada demasiado restrictiva para texto plano

Prioridad: **P1**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Se separa la validacion de claves y payloads. El texto plano `null` es invalido, pero cadenas vacias o blancas se pueden cifrar.

### H04 - Checkstyle no bloquea y acumula deuda conocida

Prioridad: **P2**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Se corrige la linea base a 0 violaciones y `failOnViolation` queda en `true`.

### H05 - Dependencias de test y plugins tienen actualizaciones estables disponibles

Prioridad: **P2**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Se actualizan versiones estables prudentes y se evita subir a versiones alpha, milestone o majors no necesarias.

### H06 - Java local no esta en el ultimo parche publicado de la familia 21

Prioridad: **P2**
Estado: **Bloqueado**
Fecha revision: 2026-08-12

Actualizar el JDK instalado en la maquina local no es un cambio de repositorio. El README deja documentado que se recomienda usar el ultimo parche de Java 21 LTS. CI mantiene `java-version: '21'` para recibir una distribucion actual de Temurin.

### H07 - Maven Wrapper no esta en la ultima version estable de Maven 3.9.x

Prioridad: **P3**
Estado: **Hecho**
Fecha cierre: 2026-08-12

`.mvn/wrapper/maven-wrapper.properties` apunta a Maven 3.9.16.

### H08 - CI ejecuta dos builds completos

Prioridad: **P3**
Estado: **Hecho**
Fecha cierre: 2026-08-12

CI queda simplificado a un unico comando `./mvnw -B -Pquality clean verify`.

### H09 - Release hace deploy saltando tests despues de haber construido

Prioridad: **P3**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Release queda con `./mvnw -B -Pquality clean deploy`, de forma que compila, testea, ejecuta calidad y despliega en una misma ejecucion.

### H10 - Lombok aporta poco valor y condiciona consumidores/builds

Prioridad: **P3**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Lombok se retira del POM y del codigo. Los loggers quedan declarados explicitamente con SLF4J.


### H11 - Acciones GitHub deprecadas por runtime Node 20

Prioridad: **P2**
Estado: **Hecho**
Fecha cierre: 2026-08-12

Los workflows usaban `actions/checkout@v4` y `actions/setup-java@v4`, que GitHub marco con avisos por runtime Node 20 y deprecacion de `setup-java@v4`. Se actualizan a `actions/checkout@v7` y `actions/setup-java@v5` para usar majors vigentes compatibles con Node 24.

### H12 - Dependabot Updates no configurado

Prioridad: **P2**
Estado: **Hecho**
Fecha cierre: 2026-09-08

Se anade `.github/dependabot.yml` para revisar semanalmente dependencias Maven y acciones de GitHub. Los updates se agrupan por ecosistema, usan `main` como rama objetivo y etiquetan los PRs como `dependencies`, `java` o `github-actions` segun corresponda.
## Actualizaciones aplicadas

### Plataforma

| Componente | Antes | Despues | Estado |
| --- | --- | --- | --- |
| Java local | 21.0.9 | Accion externa | Bloqueado fuera del repo |
| Maven Wrapper | 3.9.15 | 3.9.16 | Hecho |
| Maven minimo POM | 3.6.3 | 3.6.3 | Mantenido |

### Dependencias Maven

| Propiedad | Antes | Despues | Estado |
| --- | --- | --- | --- |
| `logback.version` | 1.5.24 | 1.6.2 | Hecho |
| `lombok.version` | 1.18.42 | Retirado | Hecho |
| `junit.version` | 5.8.2 | 5.14.4 | Hecho |
| `assertj.version` | 3.21.0 | 3.27.7 | Hecho |
| `slf4j.version` | 2.0.17 | 2.0.17 | Mantenido, se evita alpha |
| `jasypt.version` | 1.9.3 | 1.9.3 | Mantenido solo para legado |

### Plugins Maven

| Propiedad | Antes | Despues | Estado |
| --- | --- | --- | --- |
| `spotbugs.plugin.version` | 4.9.8.2 | 4.10.3.0 | Hecho |
| `maven.compiler.plugin.version` | 3.14.1 | 3.15.0 | Hecho |
| `maven.dependency.plugin.version` | 3.9.0 | 3.11.0 | Hecho |
| `maven.enforcer.plugin.version` | 3.6.2 | 3.6.3 | Hecho |
| `maven.surefire.plugin.version` | 3.5.4 | 3.5.4 | Mantenido, se evita milestone |
| `versions.plugin.version` | 2.20.1 | 2.21.0 | Hecho |
| `maven.javadoc.plugin.version` | 3.12.0 | 3.12.0 | Mantenido |
| `maven.source.plugin.version` | 3.4.0 | 3.4.0 | Mantenido, se evita beta Maven 4 |

## Matriz de hitos vivos

| ID | Hito | Prioridad | Estado | Evidencia | Fecha cierre |
| --- | --- | --- | --- | --- | --- |
| AV-01 | Definir modelo criptografico objetivo y formato versionado | P0 | Hecho | `decision-criptografia-2026-08-12.md` | 2026-08-12 |
| AV-02 | Implementar cifrado autenticado manteniendo lectura legado | P0 | Hecho | Tests `EH2` y legado Jasypt | 2026-08-12 |
| AV-03 | Eliminar `getEncryptKey()` o sustituirlo por `hasEncryptKeyConfigured()` | P1 | Hecho | API actualizada y tests ajustados | 2026-08-12 |
| AV-04 | Separar validacion de claves y payloads | P1 | Hecho | Tests de payload vacio/blanco | 2026-08-12 |
| AV-05 | Actualizar dependencias estables de test y build | P2 | Hecho | POM actualizado, quality verde | 2026-08-12 |
| AV-06 | Actualizar JDK local recomendado a ultimo parche Java 21 LTS | P2 | Bloqueado | Requiere actualizar JDK instalado fuera del repo |  |
| AV-07 | Actualizar Maven Wrapper a 3.9.16 | P3 | Hecho | Wrapper actualizado | 2026-08-12 |
| AV-08 | Reducir Checkstyle a 0 avisos | P2 | Hecho | `-Pquality verify`: 0 violaciones | 2026-08-12 |
| AV-09 | Hacer Checkstyle bloqueante | P2 | Hecho | `failOnViolation=true` | 2026-08-12 |
| AV-10 | Simplificar CI para evitar builds duplicados | P3 | Hecho | Workflow CI actualizado | 2026-08-12 |
| AV-11 | Revisar release para publicar exactamente artefactos verificados | P3 | Hecho | Release ejecuta `clean deploy` con `quality` | 2026-08-12 |
| AV-12 | Evaluar retirada de Lombok | P3 | Hecho | Lombok retirado de POM y codigo | 2026-08-12 |
| AV-13 | Migrar GitHub Actions de Node 20/deprecadas a majors compatibles con Node 24 | P2 | Hecho | `actions/checkout@v7`, `actions/setup-java@v5` | 2026-08-12 |
| AV-14 | Podar ramas mergeadas y dejar estructura profesional de ramas | P2 | Hecho | GitHub conserva solo `main`; releases mediante tags | 2026-08-12 |
| AV-15 | Activar Dependabot Updates para Maven y GitHub Actions | P2 | Hecho | `.github/dependabot.yml` con revision semanal | 2026-09-08 |

## Riesgo por area actualizado

| Area | Riesgo actual | Motivo |
| --- | --- | --- |
| Seguridad criptografica | Medio-bajo | Nuevos cifrados autenticados; legado pendiente de retirada futura |
| Manejo de secretos | Bajo | La API ya no expone claves en claro |
| Build y reproducibilidad | Bajo | Maven Wrapper actualizado y CI presente |
| Calidad estatica | Bajo | SpotBugs limpio y Checkstyle bloqueante con 0 violaciones |
| Testing | Bajo-medio | Tests cubren contrato principal, autenticidad, payload vacio y legado |
| Dependencias | Bajo-medio | Updates estables aplicados; Jasypt queda por compatibilidad legado |
| Documentacion | Bajo | README, changelog y ADR actualizados |
| CI/CD | Bajo | CI y release verifican calidad antes de publicar; acciones oficiales migradas a majors compatibles con Node 24 y Dependabot Updates activo |
| Estructura de ramas | Bajo | GitHub queda con `main` como unica rama activa; trabajo futuro mediante ramas `jarp/*` y PR |

## Comandos de mantenimiento

```powershell
.\mvnw.cmd -B test
.\mvnw.cmd -B -Pquality verify
.\mvnw.cmd -B dependency:tree
.\mvnw.cmd -B versions:display-dependency-updates -DallowSnapshots=false -DallowMajorUpdates=false
.\mvnw.cmd -B versions:display-plugin-updates -DallowSnapshots=false -DallowMajorUpdates=false
```

## Criterios de aceptacion

| Criterio | Estado | Evidencia |
| --- | --- | --- |
| Existe decision criptografica actualizada | Hecho | `decision-criptografia-2026-08-12.md` |
| La implementacion nueva usa cifrado autenticado | Hecho | AES-GCM en `EncryptorServiceImpl` |
| Hay compatibilidad documentada con textos anteriores | Hecho | README, ADR y test legado |
| La API no expone claves en claro | Hecho | `getEncryptKey()` eliminado |
| `test` y `-Pquality verify` pasan sin avisos relevantes | Hecho | 14 tests, SpotBugs 0, Checkstyle 0 |
| Checkstyle bloquea nuevas regresiones | Hecho | `failOnViolation=true` |
| Dependencias y plugins estan en versiones estables vigentes | Hecho | POM actualizado evitando alpha/milestone |
| JDK local alineado con ultimo parche Java 21 | Bloqueado | Accion externa al repositorio |

## Historial de hitos cerrados

- 2026-08-12: AV-01 cerrado con nueva decision criptografica.
- 2026-08-12: AV-02 cerrado con implementacion `EH2(...)` y compatibilidad Jasypt legado.
- 2026-08-12: AV-03 cerrado eliminando exposicion publica de clave.
- 2026-08-12: AV-04 cerrado separando validaciones de clave y payload.
- 2026-08-12: AV-05 cerrado con actualizaciones estables de dependencias y plugins.
- 2026-08-12: AV-07 cerrado actualizando Maven Wrapper a 3.9.16.
- 2026-08-12: AV-08 y AV-09 cerrados con Checkstyle bloqueante y 0 violaciones.
- 2026-08-12: AV-10 y AV-11 cerrados ajustando CI y release.
- 2026-08-12: AV-12 cerrado retirando Lombok.
- 2026-08-12: AV-13 cerrado migrando acciones oficiales de GitHub a majors compatibles con Node 24.
- 2026-08-12: AV-14 cerrado podando ramas mergeadas y dejando `main` como unica rama activa en GitHub.
- 2026-09-08: AV-15 cerrado activando Dependabot Updates para Maven y GitHub Actions.
