-- Seed api/v1: catalogo de especies (PK auto; PERRO queda con id 1, orden de insercion).
-- 30-mascota.sql usa especie_id 1 (PERRO) y 2 (GATO).
-- Idempotente: sin INSERTs con id fijo y sin duplicados aunque se ejecute en cada arranque.

INSERT INTO especie (nombre)
SELECT 'PERRO' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'PERRO');

INSERT INTO especie (nombre)
SELECT 'GATO' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'GATO');

INSERT INTO especie (nombre)
SELECT 'AVE' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'AVE');

INSERT INTO especie (nombre)
SELECT 'ROEDOR' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'ROEDOR');

INSERT INTO especie (nombre)
SELECT 'REPTIL' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'REPTIL');

INSERT INTO especie (nombre)
SELECT 'OTRO' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'OTRO');
