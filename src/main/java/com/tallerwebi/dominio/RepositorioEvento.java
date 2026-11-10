package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioEvento {
  void guardar(Evento evento);

  void actualizar(Evento evento);

  Evento buscarPorId(Long id);

  List<ServicioAdicional> buscarServicio(Long eventoId);

  boolean tieneServicio(Long eventoId, Long servicioId);

  void agregarServicio(Evento evento, ServicioAdicional servicio);

  void quitarServicio(Long eventoId, Long servicioId);
}
