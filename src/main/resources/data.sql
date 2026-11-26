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

-- EVENTO YA REALIZADO DEL USUARIO TEST (para probar publicarlo como paquete) --
INSERT INTO Evento(fecha, tipoPaquete, cantidadInvitados, salon_id, catering_id)
VALUES ('2026-09-12', 'CUMPLEANIOS', 40,
        (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
        (SELECT id FROM Catering WHERE nombre = 'Catering Basico'));

INSERT INTO EventoServicio(evento_id, servicio_id)
SELECT e.id, s.id FROM Evento e, ServicioAdicional s
WHERE e.fecha = '2026-09-12' AND s.nombre IN ('DJ', 'Fotografia');

-- Presupuesto: salon 50000 + catering 1500 x 40 + DJ 40000 + Fotografia 30000 = 180000
INSERT INTO Reserva(usuario_id, evento_id, presupuesto, estado, tipoPago, montoAbonado)
VALUES ((SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'),
        (SELECT id FROM Evento WHERE fecha = '2026-09-12'),
        180000.0, 'FINALIZADA', 'TOTAL', 180000.0);


-- EVENTOS YA REALIZADOS DESDE EL PAQUETE "Básico" (para probar reseñas) --
-- Dos ya reseñados (se ven en /paquetes-predeterminados/{id del Básico}) y uno sin reseñar
-- (se puede reseñar desde Mis reservas).
INSERT INTO Evento(fecha, tipoPaquete, cantidadInvitados, salon_id, catering_id, paqueteOrigen_id)
SELECT '2026-06-20', 'CUMPLEANIOS', 40, p.salon_id, p.catering_id, p.id FROM Paquete p WHERE p.nombre = 'Básico';

INSERT INTO Evento(fecha, tipoPaquete, cantidadInvitados, salon_id, catering_id, paqueteOrigen_id)
SELECT '2026-07-05', 'CUMPLEANIOS', 40, p.salon_id, p.catering_id, p.id FROM Paquete p WHERE p.nombre = 'Básico';

INSERT INTO Evento(fecha, tipoPaquete, cantidadInvitados, salon_id, catering_id, paqueteOrigen_id)
SELECT '2026-08-15', 'CUMPLEANIOS', 40, p.salon_id, p.catering_id, p.id FROM Paquete p WHERE p.nombre = 'Básico';

INSERT INTO Reserva(usuario_id, evento_id, presupuesto, estado, tipoPago, montoAbonado)
SELECT u.id, e.id, 165000.0, 'FINALIZADA', 'TOTAL', 165000.0
FROM Usuario u, Evento e
WHERE u.email = 'test@unlam.edu.ar' AND e.fecha IN ('2026-06-20', '2026-07-05', '2026-08-15');

INSERT INTO Resenia(reserva_id, puntaje, comentario, fecha)
SELECT r.id, 5, 'Excelente atención, el salón impecable y la comida muy rica.', '2026-07-07'
FROM Reserva r JOIN Evento e ON r.evento_id = e.id WHERE e.fecha = '2026-07-05';

INSERT INTO Resenia(reserva_id, puntaje, comentario, fecha)
SELECT r.id, 4, 'Muy lindo todo. El DJ llegó un poco tarde.', '2026-06-22'
FROM Reserva r JOIN Evento e ON r.evento_id = e.id WHERE e.fecha = '2026-06-20';

-- RESERVAS FUTURAS DEL USUARIO TEST (para probar cancelar reserva) --
-- Las fechas son relativas a hoy (la base se recrea en cada arranque),
-- así que nunca se vencen.

-- 1) Reserva SEÑADA con evento a 30 días: se puede cancelar (aparece el botón)
--    y además admite abonos. Presupuesto 165000, seña 30% = 49500.
INSERT INTO Evento(fecha, tipoPaquete, cantidadInvitados, salon_id, catering_id, paqueteOrigen_id)
SELECT DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'CUMPLEANIOS', 40, p.salon_id, p.catering_id, p.id
FROM Paquete p WHERE p.nombre = 'Básico';

INSERT INTO Reserva(usuario_id, evento_id, presupuesto, estado, tipoPago, montoAbonado)
VALUES ((SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'),
        (SELECT id FROM Evento WHERE fecha = DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
        165000.0, 'SENIADA', 'SENIA', 49500.0);

-- 2) Reserva CONFIRMADA con evento a 6 días: NO se puede cancelar (falta menos de 7),
--    no aparece el botón y se ve el aviso.
--    Presupuesto: salon Jardin del Sol 90000 + Catering Medio 3000 x 30 = 180000.
INSERT INTO Evento(fecha, tipoPaquete, cantidadInvitados, salon_id, catering_id)
VALUES (DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'FIESTA_DE_15', 30,
        (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
        (SELECT id FROM Catering WHERE nombre = 'Catering Medio'));

INSERT INTO Reserva(usuario_id, evento_id, presupuesto, estado, tipoPago, montoAbonado)
VALUES ((SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'),
        (SELECT id FROM Evento WHERE fecha = DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
        180000.0, 'CONFIRMADA', 'TOTAL', 180000.0);

-- =========================================================
-- NUEVOS SERVICIOS ADICIONALES
-- =========================================================
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'DJ + Iluminación LED', 65000.0, false, 'DJ');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Sonido e Iluminación Ambiental', 25000.0, false, 'DJ');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Cabina de Fotos', 45000.0, false, 'FOTOGRAFIA');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Book de Fotos Pre-Evento', 35000.0, false, 'FOTOGRAFIA');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Video con Drone', 60000.0, false, 'VIDEO');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Plataforma 360', 50000.0, false, 'VIDEO');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Ambientación Temática', 40000.0, false, 'DECORACION');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Centros de Mesa y Arreglos Florales', 350.0, true, 'DECORACION');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Cotillón Luminoso LED', 900.0, true, 'COTILLON');
INSERT INTO ServicioAdicional(id, nombre, precio, precioPorInvitado, categoriaServicio) VALUES(null, 'Show en Vivo / Animación Musical', 55000.0, false, 'ANIMACION');

-- =========================================================
-- NUEVOS PAQUETES PREDETERMINADOS (predeterminado = true)
-- =========================================================
INSERT INTO Paquete(nombre, tipo, salon_id, catering_id, precioEstimado, predeterminado)
VALUES
    ('Cumpleaños VIP', 'CUMPLEANIOS',
     (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Medio'), 552000.0, true),
    ('Quince Soñado', 'FIESTA_DE_15',
     (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Completo'), 1048000.0, true),
    ('Boda al Aire Libre', 'CASAMIENTO',
     (SELECT id FROM Salon WHERE nombre = 'Salon Jardin del Sol'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Completo'), 633000.0, true);

-- =========================================================
-- NUEVOS PAQUETES ADICIONALES (predeterminado = false)
-- =========================================================
INSERT INTO Paquete(nombre, tipo, salon_id, catering_id, precioEstimado, predeterminado)
VALUES
    ('Cumpleaños Infantil Full', 'CUMPLEANIOS',
     (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Basico'), 225000.0, false),
    ('Quince Teen Party', 'FIESTA_DE_15',
     (SELECT id FROM Salon WHERE nombre = 'Salon Los Alamos'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Medio'), 360000.0, false),
    ('Boda Gala Nocturna', 'CASAMIENTO',
     (SELECT id FROM Salon WHERE nombre = 'Salon Gran Bahia'),
     (SELECT id FROM Catering WHERE nombre = 'Catering Completo'), 987000.0, false);

-- =========================================================
-- SERVICIOS DE LOS NUEVOS PAQUETES
-- =========================================================
INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Cumpleaños VIP'
  AND s.nombre IN ('DJ + Iluminación LED', 'Cabina de Fotos', 'Ambientación Temática', 'Cotillón Luminoso LED');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Quince Soñado'
  AND s.nombre IN ('DJ + Iluminación LED', 'Book de Fotos Pre-Evento', 'Plataforma 360', 'Ambientación Temática', 'Cotillón Luminoso LED');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Boda al Aire Libre'
  AND s.nombre IN ('Sonido e Iluminación Ambiental', 'Fotografia', 'Video con Drone', 'Centros de Mesa y Arreglos Florales');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Cumpleaños Infantil Full'
  AND s.nombre IN ('Animacion', 'Ambientación Temática', 'Cotillon');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Quince Teen Party'
  AND s.nombre IN ('DJ + Iluminación LED', 'Plataforma 360', 'Cotillón Luminoso LED');

INSERT INTO Paquete_Servicios(paquete_id, servicio_id)
SELECT p.id, s.id FROM Paquete p, ServicioAdicional s
WHERE p.nombre = 'Boda Gala Nocturna'
  AND s.nombre IN ('DJ + Iluminación LED', 'Fotografia', 'Video', 'Show en Vivo / Animación Musical', 'Centros de Mesa y Arreglos Florales');