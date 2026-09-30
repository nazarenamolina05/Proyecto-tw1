package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioSalon {
  List<Salon> obtenerTodos();

  Salon buscarPorId(Long id);
}
