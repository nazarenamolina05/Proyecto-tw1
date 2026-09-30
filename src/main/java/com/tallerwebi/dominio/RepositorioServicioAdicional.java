package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioServicioAdicional {
  List<ServicioAdicional> obtenerTodos();

  ServicioAdicional buscarPorId(Long id);
}
