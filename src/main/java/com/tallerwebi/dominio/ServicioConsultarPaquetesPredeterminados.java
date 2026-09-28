package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioConsultarPaquetesPredeterminados {
  List<Paquete> consultarTodos();
  Paquete consultarPorId(Long id);
}
