package com.tallerwebi.dominio;

public enum TipoPaquete {
  CUMPLEANIOS("Cumpleaños"),
  FIESTA_DE_15("Fiesta de 15"),
  CASAMIENTO("Casamiento"),
  EMPRESARIAL("Empresarial");

  private final String etiqueta;

  TipoPaquete(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  public String getEtiqueta() {
    return etiqueta;
  }
}
