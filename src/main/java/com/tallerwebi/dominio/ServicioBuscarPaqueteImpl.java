package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServicioBuscarPaqueteImpl implements ServicioBuscarPaquete {

  private RepositorioPaquete repositorioPaquete;

  public ServicioBuscarPaqueteImpl(RepositorioPaquete repositorioPaquete) {
    this.repositorioPaquete = repositorioPaquete;
  }

  @Override
  public List<Paquete> buscarPorTipo(TipoPaquete tipoPaquete) {
    return buscarPorFiltros(tipoPaquete, null, null);
  }

  @Override
  public Paquete consultarPorId(Long id) {
    Paquete paquete = repositorioPaquete.obtenerPorId(id);
    if (paquete == null) {
      throw new PaqueteNoEncontrado();
    }
    return paquete;
  }

  @Override
  public List<Paquete> buscarPorFiltros(TipoPaquete tipo, Double precioMaximo, Integer invitados) {
    return repositorioPaquete.buscarPorFiltros(tipo, precioMaximo, invitados);
  }
}
