-- Seed api/v1: estados del ciclo de vida de las citas (Constantes de EstadoCita:
-- PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA). POST /api/v1/usuario/citas
-- requiere PENDIENTE; PATCH la transicion con CONFIRMADA/CANCELADA/COMPLETADA.
-- Idempotente: sin INSERTs con id fijo y sin duplicados aunque se ejecute en cada arranque.

INSERT INTO estado_cita (nombre)
SELECT 'PENDIENTE' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM estado_cita WHERE nombre = 'PENDIENTE');

INSERT INTO estado_cita (nombre)
SELECT 'CONFIRMADA' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM estado_cita WHERE nombre = 'CONFIRMADA');

INSERT INTO estado_cita (nombre)
SELECT 'CANCELADA' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM estado_cita WHERE nombre = 'CANCELADA');

INSERT INTO estado_cita (nombre)
SELECT 'COMPLETADA' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM estado_cita WHERE nombre = 'COMPLETADA');
