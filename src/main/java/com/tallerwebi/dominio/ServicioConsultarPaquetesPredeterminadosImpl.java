package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import java.util.List;
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
}
