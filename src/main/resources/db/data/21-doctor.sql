-- P6: semillas de la tabla doctor (guia seccion 5: 4 doctores activos, uno por
-- especialidad). Idempotente: no duplica si se ejecuta en cada arranque de MySQL.
-- La especialidad se resuelve por nombre (no por id) para no depender del orden
-- de insercion del catalogo.

INSERT INTO doctor (especialidad_id, nombre, apellido, email, telefono, numero_licencia, activo)
SELECT e.id, 'Carlos Miguel', 'Ramirez Soto', 'carlos.ramirez@vetlife.com', '987654321', 'CM-1001', 1
FROM especialidad e
WHERE e.nombre = 'Medicina general'
  AND NOT EXISTS (SELECT 1 FROM doctor d WHERE d.email = 'carlos.ramirez@vetlife.com'
                 OR d.numero_licencia = 'CM-1001');

INSERT INTO doctor (especialidad_id, nombre, apellido, email, telefono, numero_licencia, activo)
SELECT e.id, 'Ana Lucia', 'Torres Vega', 'ana.torres@vetlife.com', '987654322', 'CM-1002', 1
FROM especialidad e
WHERE e.nombre = 'Cirugia'
  AND NOT EXISTS (SELECT 1 FROM doctor d WHERE d.email = 'ana.torres@vetlife.com'
                 OR d.numero_licencia = 'CM-1002');

INSERT INTO doctor (especialidad_id, nombre, apellido, email, telefono, numero_licencia, activo)
SELECT e.id, 'Luis Enrique', 'Galvez Mora', 'luis.galvez@vetlife.com', '987654323', 'CM-1003', 1
FROM especialidad e
WHERE e.nombre = 'Dermatologia'
  AND NOT EXISTS (SELECT 1 FROM doctor d WHERE d.email = 'luis.galvez@vetlife.com'
                 OR d.numero_licencia = 'CM-1003');

INSERT INTO doctor (especialidad_id, nombre, apellido, email, telefono, numero_licencia, activo)
SELECT e.id, 'Sofia Elena', 'Delgado Cruz', 'sofia.delgado@vetlife.com', '987654324', 'CM-1004', 1
FROM especialidad e
WHERE e.nombre = 'Cardiologia'
  AND NOT EXISTS (SELECT 1 FROM doctor d WHERE d.email = 'sofia.delgado@vetlife.com'
                 OR d.numero_licencia = 'CM-1004');
