-- 6 servicios
INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-100', 'Consulta general', 'Medicina General', '30 min', 35.00, 'bi-clipboard-heart', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-100');

INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-101', 'Vacunación', 'Prevención', '15 min', 25.00, 'bi-shield-plus', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-101');

INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-102', 'Desparasitación', 'Prevención', '15 min', 20.00, 'bi-bug', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-102');

INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-103', 'Cirugía', 'Cirugía', '1 h', 150.00, 'bi-scissors', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-103');

INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-104', 'Peluquería', 'Estética', '45 min', 40.00, 'bi-scissors', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-104');

INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-105', 'Baño', 'Estética', '30 min', 25.00, 'bi-droplet', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-105');

INSERT INTO servicio (id, nombre, categoria, duracion, precio, icono, activo)
SELECT 'S-106', 'Hospitalización', 'Cuidados', '1 día(s)', 120.00, 'bi-hospital', true FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM servicio WHERE id = 'S-106');
