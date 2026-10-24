package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioBuscarPaquete {
  List<Paquete> buscarPorTipo(TipoPaquete tipoPaquete);

  Paquete consultarPorId(Long id);

  List<Paquete> buscarPorFiltros(TipoPaquete tipoPaquete, Double presupuesto, Integer invitados);
}
