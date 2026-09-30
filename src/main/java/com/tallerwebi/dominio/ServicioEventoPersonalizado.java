package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface ServicioEventoPersonalizado {
  EventoPersonalizado crearEvento(TipoPaquete tipo, LocalDate fecha, Integer cantidadInvitados);

  List<Salon> obtenerSalones();

  List<Catering> obtenerCaterings();

  List<ServicioAdicional> obtenerServicioAdicionales();

  void elegirSalon(EventoPersonalizado evento, Long idSalon);

  void elegirCatering(EventoPersonalizado evento, Long idCatering);

  void agregarServicio(EventoPersonalizado evento, Long idServicio);

  void quitarServicio(EventoPersonalizado evento, Long idServicio);
}
