# Decision criptografica 2026-08-12

Fecha: 2026-08-12  
Proyecto: `encrypt-helper`  
Version objetivo: `7.0.0`

## Contexto

La auditoria viva de 2026-08-12 identifico que `BasicTextEncryptor` no ofrece un contrato criptografico suficientemente explicito para secretos productivos de alto impacto. Tambien constaba que el descifrado con clave incorrecta no debia interpretarse como una prueba fuerte de autenticidad.

## Decision

A partir de la version 7.0.0, los nuevos cifrados usan formato versionado `EH2(...)` con:

- AES-GCM (`AES/GCM/NoPadding`).
- Etiqueta de autenticacion de 128 bits.
- PBKDF2-HMAC-SHA256.
- Salt aleatorio de 16 bytes.
- IV aleatorio de 12 bytes.
- Clave AES de 256 bits.
- 210.000 iteraciones PBKDF2.
- Base64 URL-safe sin padding para serializar salt, IV y texto cifrado.

El descifrado mantiene compatibilidad con textos legados sin prefijo `EH2(` usando Jasypt `BasicTextEncryptor`. Esa ruta existe solo para migracion y lectura de datos antiguos.

## Consecuencias

- Los nuevos textos cifrados son autenticados: una clave incorrecta o manipulacion del ciphertext debe fallar.
- El formato queda versionado para permitir futuras migraciones.
- Jasypt se mantiene como dependencia mientras exista soporte de lectura legado.
- La API deja de exponer la clave configurada mediante `getEncryptKey()`.

## Criterio de retirada del legado

Antes de retirar Jasypt se debe confirmar que no existen textos cifrados antiguos en uso, o que han sido migrados a `EH2(...)`. Esa retirada debe hacerse en otra version mayor si afecta a consumidores.
