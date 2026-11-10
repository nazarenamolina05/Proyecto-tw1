package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface ServicioEvento {
  Evento crearEvento(TipoPaquete tipoPaquete, LocalDate fecha, Integer cantidadInvitados);

  // [REFACTOR EVENTO] Crea el evento a partir de un paquete y le copia sus servicios.
  Evento crearEventoDesdePaquete(
    Paquete paquete,
    TipoPaquete tipoPaquete,
    LocalDate fecha,
    Integer cantidadInvitados
  );

  // [REFACTOR EVENTO] Antes el controlador hacía los set directamente sobre el evento.
  void actualizarDatos(
    Evento evento,
    TipoPaquete tipoPaquete,
    LocalDate fecha,
    Integer cantidadInvitados
  );

  List<Salon> obtenerSalones();
  List<Catering> obtenerCaterings();
  List<ServicioAdicional> obtenerServiciosAdicionales();

  // [REFACTOR EVENTO] Reemplaza a evento.getServiciosAdicionales().
  List<ServicioAdicional> obtenerServiciosDe(Evento evento);

  void elegirSalon(Evento evento, Long idSalon);
  void elegirCatering(Evento evento, Long idCatering);
  void agregarServicio(Evento evento, Long idServicio);
  void quitarServicio(Evento evento, Long idServicio);
}
