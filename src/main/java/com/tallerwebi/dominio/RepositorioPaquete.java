package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface RepositorioPaquete {
  public List<Paquete> obtenerTodos();
}
