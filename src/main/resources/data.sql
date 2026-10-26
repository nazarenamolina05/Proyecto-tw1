INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true);

-- SALON DATOS --
INSERT INTO Salon(id, nombre, precio, capacidad) VALUES(null, 'Salon Gran Bahia', 150000.0, 120);
INSERT INTO Salon(id, nombre, precio, capacidad) VALUES(null, 'Salon Jardin del Sol', 90000.0, 80);
INSERT INTO Salon(id, nombre, precio, capacidad) VALUES(null, 'Salon Los Alamos', 50000.0, 50);

-- CATERING DATOS--
INSERT INTO Catering(id, nombre, precioPorPersona) VALUES(null, 'Catering Completo', 5000.0);
INSERT INTO Catering(id, nombre, precioPorPersona) VALUES(null, 'Catering Medio', 3000.0);
INSERT INTO Catering(id, nombre, precioPorPersona) VALUES(null, 'Catering Basico', 1500.0);

INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Empanadas', 'ENTRADA', false, false ,true);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Tabla de fiambres', 'ENTRADA', false ,true, false);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Milanesa con papas', 'PRINCIPAL', false, false, true);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Asado con ensalada', 'PRINCIPAL', false, true, false);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Helado', 'POSTRE', false, true, true);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Torta', 'POSTRE', false, false, true);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Gaseosas', 'BEBIDA', true, true, true);
INSERT INTO Comida(id, nombre, tipoComida, vegetariana, aptaCeliacos, infantil) VALUES(null, 'Agua mineral', 'BEBIDA', true, true, true );


-- CATERING_COMIDA (que incluye cada menu) --
-- Basico: un principal y una bebida
INSERT INTO catering_comida(catering_id, comida_id)
SELECT c.id, m.id FROM Catering c, Comida m
WHERE c.nombre = 'Catering Basico' AND m.nombre IN ('Milanesa con papas', 'Gaseosas');

-- Medio: entrada, principal y bebida (sin postre)
INSERT INTO catering_comida(catering_id, comida_id)
SELECT c.id, m.id FROM Catering c, Comida m
WHERE c.nombre = 'Catering Medio' AND m.nombre IN ('Empanadas', 'Asado con ensalada', 'Gaseosas');

-- Completo: todas las comidas
INSERT INTO catering_comida(catering_id, comida_id)
SELECT c.id, m.id FROM Catering c, Comida m
WHERE c.nombre = 'Catering Completo';


-- SERVICIO ADICIONAL DATOS ---
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'DJ', 40000.0, false, 'DJ');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Fotografia', 30000.0, false, 'FOTOGRAFIA');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Video', 45000.0, false, 'VIDEO');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Decoracion', 25000.0, false, 'DECORACION');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Cotillon', 500.0, true, 'COTILLON');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Animacion', 35000.0, false , 'ANIMACION');

--PAQUETES PREDETERMINADOS--
INSERT INTO Paquete(nombre, tipo, salon_id, catering_id, precioEstimado, predeterminado)
VALUES
    ('Básico', 'CUMPLEANIOS',
     (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Basico'), 165000.0, true),
    ('Estándar', 'FIESTA_DE_15',
     (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Medio'), 400000.0, true),
    ('Premium', 'CASAMIENTO',
     (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Completo'), 890000.0, true);

-- PAQUETES ADICIONALES --
INSERT INTO Paquete(nombre, tipo, salon_id, catering_id, precioEstimado, predeterminado)
VALUES
    ('Casamiento clásico', 'CASAMIENTO',
     (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Completo'), 750000.0, false),
    ('Cumpleaños familiar', 'CUMPLEANIOS',
     (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Basico'), 125000.0, false),
    ('Fiesta de quince', 'FIESTA_DE_15',
     (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Medio'), 330000.0, false),
    ('Cumpleaños Premium', 'CUMPLEANIOS',
     (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Completo'), 750000.0, false);

-- SERVICIOS DE LOS PAQUETES PREDETERMINADOS --
INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Básico' AND s.nombre IN ('DJ');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Estándar' AND s.nombre IN ('DJ', 'Fotografia');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Premium' AND s.nombre IN ('DJ', 'Fotografia', 'Video', 'Decoracion');
