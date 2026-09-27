package com.tallerwebi.dominio;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioConsultarPaquetesPredeterminadosImpl
  implements ServicioConsultarPaquetesPredeterminados {

  private RepositorioPaquete repositorioPaquete;

  public ServicioConsultarPaquetesPredeterminadosImpl(RepositorioPaquete repositorioPaquete) {
    this.repositorioPaquete = repositorioPaquete;
  }

  @Override
  public List<Paquete> consultarTodos() {
    return repositorioPaquete.obtenerTodos();
  }
}
