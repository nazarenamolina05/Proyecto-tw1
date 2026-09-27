package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ServicioConsultarPaquetesPredeterminados {
  List<Paquete> consultarTodos();
}
