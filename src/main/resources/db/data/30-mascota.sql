-- Seed api/v1: mascotas demo del usuario 1 (Demo Uno) y del usuario 2 (Demo Dos);
-- requieren las semillas 05-usuario.sql y 10-especie.sql.
-- Idempotente: sin INSERTs con id fijo y sin duplicados aunque se ejecute en cada arranque.

INSERT INTO mascota (usuario_id, especie_id, nombre, raza, sexo, fecha_nacimiento, peso_kg)
SELECT 1, 1, 'Luna', 'Border Collie', 'HEMBRA', '2022-05-10', 18.50 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE usuario_id = 1 AND nombre = 'Luna');

INSERT INTO mascota (usuario_id, especie_id, nombre, raza, sexo, fecha_nacimiento, peso_kg)
SELECT 2, 2, 'Simba', 'Persa', 'MACHO', '2020-08-15', 5.20 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE usuario_id = 2 AND nombre = 'Simba');

INSERT INTO mascota (usuario_id, especie_id, nombre, raza, sexo, fecha_nacimiento, peso_kg)
SELECT 2, 1, 'Rocky', 'Labrador', 'MACHO', '2021-11-20', 25.00 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE usuario_id = 2 AND nombre = 'Rocky');
