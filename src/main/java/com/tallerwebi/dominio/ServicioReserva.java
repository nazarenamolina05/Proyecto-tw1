package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioReserva {
  List<Reserva> obtenerReservasDe(Usuario usuario);

  Reserva reservar(Usuario usuario, Evento evento, TipoPago tipoPago);

  Reserva obtenerReservaDe(Long idReserva, Usuario usuario);
}
