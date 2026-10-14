package com.tallerwebi.dominio;

import java.util.ArrayList;
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
  public List<Paquete> buscarPorFiltros(TipoPaquete tipo, Double precioMaximo, Integer invitados) {
    return repositorioPaquete.buscarPorFiltros(tipo, precioMaximo, invitados);
  }
}
