package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface ServicioValidarSeleccionPaquete {
  List<LocalDate> obtenerFechasOcupadas(Long paqueteId);

  void validar(Long paqueteId, TipoPaquete tipoEvento, LocalDate fecha, Integer cantidadInvitados);
}
