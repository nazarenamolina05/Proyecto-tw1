package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface ServicioEvento {
  Evento crearEvento(TipoPaquete tipoPaquete, LocalDate fecha, Integer cantidadInvitados);

  List<Salon> obtenerSalones();
  List<Catering> obtenerCaterings();
  List<ServicioAdicional> obtenerServiciosAdicionales();

  void elegirSalon(Evento evento, Long idSalon);
  void elegirCatering(Evento evento, Long idCatering);
  void agregarServicio(Evento evento, Long idServicio);
  void quitarServicio(Evento evento, Long idServicio);
}
