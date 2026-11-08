package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPaquete {
  List<Paquete> obtenerTodos();

  List<Paquete> obtenerPredeterminados();

  Paquete obtenerPredeterminadoPorId(Long id);

  List<Paquete> buscarPorFiltros(TipoPaquete tipo, Double precioMaximo, Integer invitados);

  void guardar(Paquete paquete);

  // [REFACTOR PAQUETE] Reemplazan al @ManyToMany: la relación se guarda y se consulta
  // a través de la clase intermedia PaqueteServicio.
  void guardarServicio(PaqueteServicio paqueteServicio);

  List<ServicioAdicional> obtenerServiciosDe(Long idPaquete);
}
