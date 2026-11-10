package com.tallerwebi.dominio.excepcion;

public class PasarelaNoDisponible extends RuntimeException {

  public PasarelaNoDisponible(String mensaje) {
    super(mensaje);
  }

  public PasarelaNoDisponible(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }

  private static final long serialVersionUID = 1L;
}
