package com.tallerwebi.dominio.excepcion;

public class PagoNoEncontrado extends RuntimeException {

  public PagoNoEncontrado() {
    super("El pago no existe");
  }

  private static final long serialVersionUID = 1L;
}
