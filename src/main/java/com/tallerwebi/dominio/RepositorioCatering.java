package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCatering {
  List<Catering> obtenerTodos();

  Catering buscarPorId(Long id);
}
