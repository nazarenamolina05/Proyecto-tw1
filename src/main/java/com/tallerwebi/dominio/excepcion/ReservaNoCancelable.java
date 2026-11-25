package com.tallerwebi.dominio.excepcion;

public class ReservaNoCancelable extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReservaNoCancelable() {
    super("La reserva no se puede cancelar");
  }
}
