package com.tallerwebi.dominio;

import java.util.List;

public class ServiciosClasificados {

  private List<ServicioAdicional> sugeridos;
  private List<ServicioAdicional> restantes;

  public ServiciosClasificados(
    List<ServicioAdicional> sugeridos,
    List<ServicioAdicional> restantes
  ) {
    this.sugeridos = sugeridos;
    this.restantes = restantes;
  }

  public List<ServicioAdicional> getSugeridos() {
    return sugeridos;
  }

  public List<ServicioAdicional> getRestantes() {
    return restantes;
  }
}
