package com.tallerwebi.dominio.excepcion;

public class AbonoNoPermitido extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AbonoNoPermitido() {
    super("La reserva no admite abonos");
  }
}
