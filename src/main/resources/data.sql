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