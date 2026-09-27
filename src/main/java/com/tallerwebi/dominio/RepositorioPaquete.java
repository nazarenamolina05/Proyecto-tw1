package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPaquete {
  public List<Paquete> obtenerTodos();

  List<Paquete> obtenerPredeterminados();
}
