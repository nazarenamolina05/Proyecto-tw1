package com.tallerwebi.dominio;

import java.util.List;

//Agregué FunctionalInterface porque me tiraba un error de que las interfaces deben tener más de un mé
//todo, eliminar cuando se agreguen más métodos.

@FunctionalInterface
public interface RepositorioPaquete {
  public List<Paquete> obtenerTodos();
}
