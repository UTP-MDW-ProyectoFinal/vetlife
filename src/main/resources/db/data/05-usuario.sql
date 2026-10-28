-- Seeds api/v1: usuario demo del catalogo de usuarios. Lo necesitan las FK de
-- mascota (30-mascota.sql) y la autenticacion por headers email/password de
-- /api/v1/usuario/mascotas y /api/v1/usuario/citas (Aaron, Dayana).
-- Password "123456" con hash Argon2 de EncryptPassword (m=19456, t=2, p=1):
-- $argon2i$v=19$m=19456,t=2,p=1$QlOUwbktjiZnRwjxnnC94Q$kvDfFAkNCqiJKE0EZ+Ky0RofH2pVa0tZNlxeLwiUymo
-- Idempotente: sin INSERTs con id fijo y sin duplicados aunque se ejecute en cada arranque.

INSERT INTO usuarios (nombre, correo, password)
SELECT 'Demo Uno', 'demo.uno@vetlife.com', '$argon2i$v=19$m=19456,t=2,p=1$QlOUwbktjiZnRwjxnnC94Q$kvDfFAkNCqiJKE0EZ+Ky0RofH2pVa0tZNlxeLwiUymo' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'demo.uno@vetlife.com');

INSERT INTO usuarios (nombre, correo, password)
SELECT 'Demo Dos', 'demo.dos@vetlife.com', '$argon2i$v=19$m=19456,t=2,p=1$QlOUwbktjiZnRwjxnnC94Q$kvDfFAkNCqiJKE0EZ+Ky0RofH2pVa0tZNlxeLwiUymo' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'demo.dos@vetlife.com');
