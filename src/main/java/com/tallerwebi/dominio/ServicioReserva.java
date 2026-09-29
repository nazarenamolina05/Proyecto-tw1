package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ServicioReserva {
  List<Reserva> obtenerReservasDe(Usuario usuario);
}
