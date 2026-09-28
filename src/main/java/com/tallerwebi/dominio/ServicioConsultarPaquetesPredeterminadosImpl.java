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
    return repositorioPaquete.obtenerPredeterminados();
  }

  @Override
  public Paquete consultarPorId(Long id) {
    List<Paquete> paquetes = repositorioPaquete.obtenerPredeterminados();

    for (Paquete paquete : paquetes) {
      if (paquete.getId().equals(id)) {
        return paquete;
      }
    }
    return null;
  }
}
