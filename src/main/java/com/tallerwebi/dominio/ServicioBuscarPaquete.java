package com.tallerwebi.dominio;

import java.util.List;
import java.util.Map;

public interface ServicioBuscarPaquete {
  List<Paquete> buscarPorTipo(TipoPaquete tipoPaquete);

  List<Paquete> buscarPorFiltros(TipoPaquete tipoPaquete, Double presupuesto, Integer invitados);

  // [REFACTOR PAQUETE] Para las cards (paquete-card.html): id del paquete -> sus servicios.
  Map<Long, List<ServicioAdicional>> consultarServiciosPorPaquete(List<Paquete> paquetes);
}
