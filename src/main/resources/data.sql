INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true);

-- SALON DATOS --
INSERT INTO Salon(id, nombre, precio) VALUES(null, 'Salon Gran Bahia', 150000.0);
INSERT INTO Salon(id, nombre, precio) VALUES(null, 'Salon Jardin del Sol', 90000.0);
INSERT INTO Salon(id, nombre, precio) VALUES(null, 'Salon Los Alamos', 50000.0);

-- CATERING DATOS--
INSERT INTO Catering(id, nombre, precioPorPersona) VALUES(null, 'Catering Completo', 5000.0);
INSERT INTO Catering(id, nombre, precioPorPersona) VALUES(null, 'Catering Medio', 3000.0);
INSERT INTO Catering(id, nombre, precioPorPersona) VALUES(null, 'Catering Basico', 1500.0);


-- SERVICIO ADICIONAL DATOS ---
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado) VALUES(null, 'DJ', 40000.0, false);
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado) VALUES(null, 'Fotografia', 30000.0, false);
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado) VALUES(null, 'Video', 45000.0, false);
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado) VALUES(null, 'Decoracion', 25000.0, false);
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado) VALUES(null, 'Cotillon', 500.0, true);

--PAQUETES PREDETERMINADOS--
INSERT INTO Paquete(nombre, tipo, salon_id, catering, precioEstimado, predeterminado)
VALUES ('Básico', 'CUMPLEANIOS',
        (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
        'Catering Don José', 80000.0, true),( 'Estándar', 'FIESTA_DE_15',
        (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
        'Catering Fiesta Plena', 150000.0, true),('Premium', 'CASAMIENTO',
        (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
        'Catering Alta Cocina', 300000.0, true);

-- PAQUETES ADICIONALES --
INSERT INTO Paquete
(nombre, tipo, salon_id, catering, precioEstimado, predeterminado)
VALUES('Casamiento clásico', 'CASAMIENTO',
        (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
        'Catering Completo', 350000.0, false),
        ('Cumpleaños familiar', 'CUMPLEANIOS',
        (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
        'Catering Basico', 100000.0, false),
        ('Fiesta de quince', 'FIESTA_DE_15',
        (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
        'Catering Medio', 220000.0, false),
        ('Cumpleaños Premium', 'CUMPLEANIOS',
        (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
        'Catering Completo', 400000.0, false);

-- SERVICIOS DE LOS PAQUETES PREDETERMINADOS --
INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'DJ' FROM Paquete WHERE nombre = 'Básico';

INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'DJ' FROM Paquete WHERE nombre = 'Estándar';

INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'Fotografía' FROM Paquete WHERE nombre = 'Estándar';

INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'DJ' FROM Paquete WHERE nombre = 'Premium';

INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'Fotografía' FROM Paquete WHERE nombre = 'Premium';

INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'Video' FROM Paquete WHERE nombre = 'Premium';

INSERT INTO Paquete_Servicios (paquete_id, servicio)
SELECT id, 'Decoración' FROM Paquete WHERE nombre = 'Premium';
