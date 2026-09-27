package com.tallerwebi.dominio;

public class Paquete {

  private String nombre;
  private TipoPaquete tipo;

  public Paquete(String nombre, TipoPaquete tipo) {
    this.nombre = nombre;
    this.tipo = tipo;
  }

  public String getNombre() {
    return nombre;
  }

  public TipoPaquete getTipo() {
    return tipo;
  }
}
