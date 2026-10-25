-- P6: semillas del catalogo de especialidad (guia seccion 5: Medicina general,
-- Cirugia, Dermatologia, Cardiologia, ids 1 a 4 por orden de insercion).
-- Idempotente: sin INSERTs con id fijo y sin duplicados aunque se ejecute en cada arranque.

INSERT INTO especialidad (nombre)
SELECT 'Medicina general' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especialidad WHERE nombre = 'Medicina general');

INSERT INTO especialidad (nombre)
SELECT 'Cirugia' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especialidad WHERE nombre = 'Cirugia');

INSERT INTO especialidad (nombre)
SELECT 'Dermatologia' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especialidad WHERE nombre = 'Dermatologia');

INSERT INTO especialidad (nombre)
SELECT 'Cardiologia' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM especialidad WHERE nombre = 'Cardiologia');
