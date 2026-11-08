package com.tallerwebi.dominio;

import java.util.List;
import java.util.Map;

public interface ServicioConsultarPaquetesPredeterminados {
  List<Paquete> consultarTodos();

  Paquete consultarPorId(Long id);

  // [REFACTOR PAQUETE] Paquete ya no tiene la lista de servicios: se piden por acá.
  // Si el paquete es null (evento armado desde cero) devuelve una lista vacía.
  List<ServicioAdicional> consultarServiciosDe(Paquete paquete);

  // [REFACTOR PAQUETE] Para las cards (paquete-card.html): id del paquete -> sus servicios.
  Map<Long, List<ServicioAdicional>> consultarServiciosPorPaquete(List<Paquete> paquetes);
}
