# Plan de actuacion para aplicar la auditoria

Fecha: 2026-05-09  
Proyecto: `encrypt-helper`  
Auditoria base: `doc/auditoria/2026_05_09/auditoria-proyecto.md`

## Objetivo

Reducir el riesgo del proyecto hasta dejarlo en un estado apto para uso controlado con secretos reales, corrigiendo primero los problemas de seguridad y despues los de calidad, documentacion y entrega.

## Criterio de prioridad

- **P0 - Bloqueante:** impide considerar segura la libreria.
- **P1 - Alta:** no bloquea por si solo, pero afecta seriamente seguridad, mantenibilidad o confianza.
- **P2 - Media:** mejora estabilidad, integracion y operacion.
- **P3 - Baja:** limpieza, coherencia y mejora documental.

## Fase 0 - Preparacion y control de cambios

Prioridad: **P0**  
Duracion estimada: 0,5 dias

Tareas:

1. Crear una rama de trabajo especifica para aplicar la auditoria.
2. Confirmar si los cambios sin commit detectados antes de la auditoria son validos:
   - `pom.xml` modificado.
   - Movimiento de `EncryptDemo` a `src/test`.
   - Movimiento de `logback.xml` a `logback-test.xml`.
   - Eliminacion de ficheros SpotBugs.
3. Instalar o habilitar Maven y JDK 21 en el entorno local.
4. Anadir Maven Wrapper (`mvnw`) para que el proyecto pueda construirse sin depender de una instalacion global.
5. Ejecutar una primera linea base:
   - `./mvnw clean test`
   - `./mvnw -Pquality verify`

Criterios de aceptacion:

- Existe una rama dedicada.
- Maven Wrapper disponible en el repositorio.
- El equipo puede ejecutar el build de forma reproducible.
- Queda documentado si la linea base falla y por que.

## Fase 1 - Correcciones criticas de seguridad

Prioridad: **P0**  
Duracion estimada: 1-2 dias

### 1.1 Eliminar clave por defecto hardcodeada

Acciones:

1. Eliminar `DEFAULT_SECRET_KEY` real de `Constantes`.
2. Cambiar `EncryptorServiceImpl` para exigir clave por constructor o metodo de configuracion antes de cifrar/descifrar con clave por defecto.
3. Definir comportamiento si no hay clave configurada:
   - lanzar `EncryptorException`;
   - mensaje sin revelar datos sensibles.
4. Mantener claves de prueba solo en tests, con nombres evidentes como `TEST_SECRET_KEY`.

Criterios de aceptacion:

- No hay claves reales o reutilizables en `src/main`.
- El constructor no configura automaticamente una clave productiva.
- Usar metodos con clave por defecto sin clave configurada falla de forma controlada.

### 1.2 Eliminar logging sensible

Acciones:

1. Retirar logs que imprimen claves, textos planos, textos descifrados o argumentos genericos.
2. Cambiar `StringHelper` para no registrar el valor de la cadena validada.
3. Revisar la demo y README para no promover logging de secretos.
4. Mantener logs tecnicos de inicio/fin sin valores sensibles.

Criterios de aceptacion:

- Busqueda por `log.` no muestra claves ni textos de entrada/salida.
- La demo no imprime secretos.
- La documentacion no contiene ejemplos que registren secretos.

### 1.3 Revisar estrategia criptografica

Acciones:

1. Documentar el algoritmo efectivo usado por Jasypt.
2. Decidir si se mantiene `BasicTextEncryptor` temporalmente o se migra.
3. Si se migra, preferir una implementacion con parametros explicitos:
   - algoritmo;
   - salt;
   - iteraciones;
   - formato versionado del texto cifrado.
4. Definir compatibilidad hacia atras si ya existen datos cifrados con el formato actual.

Criterios de aceptacion:

- Hay una decision tecnica documentada.
- Si se mantiene Jasypt, sus parametros quedan claros.
- Si se migra, existe plan para textos cifrados antiguos.

## Fase 2 - Cobertura de pruebas

Prioridad: **P1**  
Duracion estimada: 1 dia

Tareas:

1. Crear tests unitarios para `EncryptorServiceImpl`.
2. Cubrir:
   - cifrado y descifrado con clave explicita;
   - cifrado y descifrado con clave configurada;
   - rechazo de texto `null`;
   - rechazo de texto blanco;
   - rechazo de clave `null`;
   - rechazo de clave blanca;
   - error al descifrar con clave incorrecta;
   - comportamiento sin clave por defecto configurada.
3. Crear tests para `StringHelper`.
4. Evitar tests que dependan de valores cifrados fijos si el cifrado usa salt.
5. Retirar o separar `EncryptDemo` para que no se confunda con un test.

Criterios de aceptacion:

- `./mvnw clean test` termina correctamente.
- Hay pruebas con aserciones reales.
- Los tests no imprimen secretos en consola.
- La cobertura cubre errores esperados y camino principal.

## Fase 3 - Calidad estatica y build reproducible

Prioridad: **P1**  
Duracion estimada: 1 dia

Tareas:

1. Restaurar o sustituir configuracion de SpotBugs/FindSecBugs.
2. Configurar Checkstyle con reglas del proyecto.
3. Hacer que el perfil `quality` ejecute verificaciones en `verify`.
4. Revisar dependencias:
   - mover `logback-classic` a scope `test`;
   - mantener `slf4j-api` para la libreria;
   - revisar versiones de JUnit y AssertJ;
   - evaluar si Lombok aporta suficiente valor en una libreria tan pequena.
5. Ejecutar:
   - `./mvnw clean verify`
   - `./mvnw -Pquality verify`

Criterios de aceptacion:

- Build limpio en entorno local.
- Perfil de calidad ejecutable y documentado.
- La libreria no fuerza una implementacion de logging al consumidor.
- No quedan ficheros de configuracion referenciados pero inexistentes.

## Fase 4 - CI/CD y releases

Prioridad: **P1**  
Duracion estimada: 0,5-1 dia

Tareas:

1. Crear workflow de CI para pull requests y pushes:
   - checkout;
   - setup Java 21;
   - cache Maven;
   - `./mvnw clean verify`;
   - `./mvnw -Pquality verify`.
2. Actualizar workflow de release:
   - compilar desde cero;
   - ejecutar tests;
   - generar JAR, sources y javadoc;
   - publicar en GitHub Packages si aplica;
   - adjuntar artefactos al release.
3. Actualizar versiones de actions usadas.
4. Evitar releases si falla el build.

Criterios de aceptacion:

- Ningun release se crea sin build y tests correctos.
- CI bloquea cambios defectuosos antes de merge.
- Los artefactos publicados corresponden al tag.

## Fase 5 - Documentacion y API publica

Prioridad: **P2**  
Duracion estimada: 0,5-1 dia

Tareas:

1. Actualizar `README.md` con:
   - coordenadas Maven reales;
   - requisito Java 21 o el requisito final decidido;
   - API actual;
   - ejemplos que compilen;
   - advertencia de no loguear secretos;
   - forma recomendada de proporcionar la clave.
2. Corregir nombres obsoletos:
   - `Encryptor` / `EncryptorImpl`;
   - `setDefaultKey` / `getDefaultKey`;
   - `encrypt(textoPlano)` / `decrypt(textoCifrado)` si no existen.
3. Documentar comandos:
   - `./mvnw test`;
   - `./mvnw verify`;
   - `./mvnw -Pquality verify`.
4. Revisar JavaDoc de la API publica.

Criterios de aceptacion:

- Los ejemplos del README compilan contra el codigo actual.
- La documentacion no promete tests, releases o funcionalidades inexistentes.
- Queda claro que la clave debe ser externa y segura.

## Fase 6 - Separacion de demo, CLI y libreria

Prioridad: **P2**  
Duracion estimada: 0,5 dias

Tareas:

1. Decidir si `EncryptDemo` debe existir como:
   - ejemplo documentado;
   - modulo CLI separado;
   - clase de test/integracion;
   - o eliminarse.
2. Mover `FinalDelProgramaHelper` fuera de `src/main` si solo aplica a CLI.
3. Eliminar `System.exit` de codigo reutilizable de libreria.
4. Si se mantiene CLI, encapsular salida del proceso en una clase `main` aislada.

Criterios de aceptacion:

- La libreria no puede terminar el proceso de una aplicacion consumidora.
- El JAR principal solo contiene API reutilizable.
- La demo no se confunde con test unitario.

## Fase 7 - Limpieza final y versionado

Prioridad: **P3**  
Duracion estimada: 0,5 dias

Tareas:

1. Corregir erratas y comentarios inconsistentes.
2. Revisar paquete y `groupId` si se va a publicar formalmente.
3. Definir si los cambios requieren version mayor por ruptura de compatibilidad.
4. Crear changelog de la correccion.
5. Preparar release candidate.

Criterios de aceptacion:

- Changelog disponible.
- Version Maven coherente con el alcance del cambio.
- No quedan referencias a API antigua en README, JavaDoc o ejemplos.

## Orden recomendado de ejecucion

1. Fase 0: preparar entorno y rama.
2. Fase 1.1 y 1.2: eliminar clave embebida y logs sensibles.
3. Fase 2: anadir tests que fijen el nuevo comportamiento.
4. Fase 3: activar calidad estatica.
5. Fase 4: asegurar CI/CD.
6. Fase 5: actualizar documentacion.
7. Fase 6: separar demo/CLI de libreria.
8. Fase 7: limpieza, versionado y release.

## Matriz de seguimiento

| ID | Tarea | Prioridad | Estado | Evidencia esperada |
| --- | --- | --- | --- | --- |
| A01 | Crear rama de actuacion | P0 | Hecho | Rama `codex-aplicar-auditoria` |
| A02 | Anadir Maven Wrapper | P0 | Hecho | `mvnw`, `mvnw.cmd`, `.mvn/wrapper` |
| A03 | Eliminar clave hardcodeada | P0 | Hecho | Sin secretos en `src/main` |
| A04 | Eliminar logs sensibles | P0 | Hecho | Revision de `log.` |
| A05 | Definir estrategia criptografica | P0 | Hecho | `decision-criptografia.md` |
| A06 | Crear tests unitarios | P1 | Hecho | `mvn test`: 10 tests, 0 fallos |
| A07 | Configurar calidad estatica | P1 | Hecho con advertencias | `mvn -Pquality verify` correcto; Checkstyle informa 120 advertencias no bloqueantes |
| A08 | Ajustar dependencias de logging | P1 | Hecho | `logback-classic` en scope `test` |
| A09 | Crear CI de build/test | P1 | Hecho | `.github/workflows/ci.yml` |
| A10 | Endurecer release workflow | P1 | Hecho | Release compila y testea antes de crear release |
| A11 | Actualizar README | P2 | Hecho | Ejemplos actualizados |
| A12 | Separar demo/CLI | P2 | Hecho | Sin `System.exit` en libreria |
| A13 | Changelog y versionado | P3 | Hecho parcialmente | `CHANGELOG.md`; pendiente decidir version final antes de release |

## Riesgos durante la aplicacion

- Cambiar la clave por defecto rompe usos existentes que dependian implicitamente de ella.
- Migrar criptografia puede impedir descifrar datos antiguos si no se define compatibilidad.
- Mover `logback-classic` puede requerir que aplicaciones consumidoras declaren su implementacion de logging.
- Activar Checkstyle o SpotBugs puede descubrir deuda adicional no visible en la auditoria inicial.

## Decision recomendada sobre compatibilidad

Como la correccion de seguridad principal cambia el comportamiento por defecto, se recomienda tratar el cambio como **version mayor** si la libreria ya esta publicada o integrada en otros proyectos. Si no hay consumidores externos, puede aplicarse directamente manteniendo la version interna, pero dejando constancia en changelog.

## Definicion de terminado

El plan se considera aplicado cuando:

- No hay secretos reales en codigo fuente ni artefactos generados.
- No se registran datos sensibles en logs.
- Hay tests unitarios reales y pasan en local y CI.
- El README refleja la API actual.
- El release workflow compila y verifica antes de publicar.
- La estrategia criptografica queda documentada y tiene plan de migracion.
