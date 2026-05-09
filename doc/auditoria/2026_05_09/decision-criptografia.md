# Decision criptografica

Fecha: 2026-05-09  
Proyecto: `encrypt-helper`

## Contexto

La auditoria detecto que la libreria usa `org.jasypt.util.text.BasicTextEncryptor` y que, antes de esta actuacion, configuraba una clave por defecto hardcodeada en codigo fuente.

La clave embebida se ha eliminado como correccion inmediata. La libreria ahora exige que la clave llegue desde la aplicacion consumidora, ya sea mediante constructor, `setEncryptKey` o parametro explicito en cada llamada.

## Decision

Se mantiene temporalmente `BasicTextEncryptor` para no romper el formato de textos cifrados existentes sin una migracion controlada.

La migracion a una estrategia criptografica con parametros explicitos queda aplazada a una version mayor o a una tarea especifica con inventario previo de datos cifrados.

Nota operativa: la implementacion actual no debe tratarse como cifrado autenticado. En pruebas se ha observado que descifrar con una clave incorrecta puede devolver texto no original en lugar de lanzar siempre una excepcion. La aplicacion consumidora no debe usar el exito de `decrypt` como prueba criptografica fuerte de autenticidad.

## Requisitos para la migracion futura

Antes de sustituir la implementacion criptografica actual se debe definir:

- Si existen textos cifrados en produccion con versiones anteriores.
- Formato versionado para distinguir cifrados antiguos y nuevos.
- Algoritmo, modo, derivacion de clave, salt e iteraciones.
- Estrategia de migracion o doble lectura.
- Pruebas de compatibilidad y reversibilidad.

## Estado recomendado

La libreria puede avanzar con las correcciones de seguridad operativa ya aplicadas, pero cualquier uso con secretos de alto impacto debe validar expresamente que `BasicTextEncryptor` cumple la politica criptografica de la organizacion.
