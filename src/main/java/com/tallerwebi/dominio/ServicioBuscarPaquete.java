package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioBuscarPaquete {
  List<Paquete> buscarPorTipo(TipoPaquete tipoPaquete);

  List<Paquete> buscarPorFiltros(TipoPaquete tipoPaquete, Double presupuesto, Integer invitados);
}
