package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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
    Paquete paquete = repositorioPaquete.obtenerPredeterminadoPorId(id);

    if (paquete == null) {
      throw new PaqueteNoEncontrado();
    }
    return paquete;
  }

  @Override
  public List<ServicioAdicional> consultarServiciosDe(Paquete paquete) {
    if (paquete == null || paquete.getId() == null) {
      return new ArrayList<>();
    }
    return repositorioPaquete.obtenerServiciosDe(paquete.getId());
  }

  @Override
  public Map<Long, List<ServicioAdicional>> consultarServiciosPorPaquete(List<Paquete> paquetes) {
    Map<Long, List<ServicioAdicional>> serviciosPorPaquete = new HashMap<>();
    for (Paquete paquete : paquetes) {
      serviciosPorPaquete.put(paquete.getId(), consultarServiciosDe(paquete));
    }
    return serviciosPorPaquete;
  }
}
