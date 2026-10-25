package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPaquete {
  List<Paquete> obtenerTodos();

  List<Paquete> obtenerPredeterminados();

  Paquete obtenerPredeterminadoPorId(Long id);

  List<Paquete> buscarPorFiltros(TipoPaquete tipo, Double precioMaximo, Integer invitados);
}
