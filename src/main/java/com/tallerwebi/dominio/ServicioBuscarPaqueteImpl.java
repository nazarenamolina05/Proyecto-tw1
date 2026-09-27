package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioBuscarPaqueteImpl implements ServicioBuscarPaquete {

  private RepositorioPaquete repositorioPaquete;

  public ServicioBuscarPaqueteImpl(RepositorioPaquete repositorioPaquete) {
    this.repositorioPaquete = repositorioPaquete;
  }

  @Override
  public List<Paquete> buscarPorTipo(TipoPaquete tipoPaquete) {
    List<Paquete> resultado = new ArrayList<>();
    for (Paquete p : repositorioPaquete.obtenerTodos()) {
      if (p.getTipo() == tipoPaquete) {
        resultado.add(p);
      }
    }
    return resultado;
  }
}
